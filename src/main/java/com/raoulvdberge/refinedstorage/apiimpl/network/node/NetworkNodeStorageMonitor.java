package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class NetworkNodeStorageMonitor extends NetworkNode implements IComparable, IType {
    public static final int DEPOSIT_ALL_MAX_DELAY = 500;

    public static final String ID = "storage_monitor";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private ItemHandlerBase itemFilter = new ItemHandlerBase(1, new ListenerNetworkNode(this)) {
        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            WorldUtils.updateBlock(world, pos);
        }
    };

    private FluidInventory fluidFilter = new FluidInventory(1, new ListenerNetworkNode(this));

    private Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;

    private int oldAmount = -1;

    public NetworkNodeStorageMonitor(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void update() {
        super.update();

        int newAmount = getAmount();

        if (oldAmount == -1) {
            oldAmount = newAmount;
        } else if (oldAmount != newAmount) {
            oldAmount = newAmount;

            WorldUtils.updateBlock(world, pos);
        }
    }

    public boolean depositAll(EntityPlayer player) {
        if (getType() != IType.ITEMS) {
            return false;
        }

        if (network == null) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return false;
        }

        Pair<ItemStack, Long> deposit = deposits.get(player.getGameProfile().getName());

        if (deposit == null) {
            return false;
        }

        ItemStack inserted = deposit.getKey();
        long insertedAt = deposit.getValue();

        if (MinecraftServer.getCurrentTimeMillis() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack toInsert = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.inventory.setInventorySlotContents(i, StackUtils.nullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));
                }
            }
        }

        return true;
    }

    public boolean deposit(EntityPlayer player, ItemStack toInsert) {
        if (network == null || toInsert == null) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return false;
        }
        if (getType() == IType.ITEMS) {
            depositItems(player, toInsert);
        } else if (getType() == IType.FLUIDS) {
            depositFluids(player, toInsert);
        }


        return true;
    }

    private void depositItems(EntityPlayer player, ItemStack toInsert) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        if (!filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, StackUtils.nullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, MinecraftServer.getCurrentTimeMillis()));
        }
    }

    private void depositFluids(EntityPlayer player, ItemStack toInsert) {
        FluidStack filter = fluidFilter.getFluid(0);
        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(toInsert, true);
        if (filter.amount == 0 || !API.instance().getComparer().isEqual(filter, result.getRight(), compare)) {
            return;
        }
        if (result.getValue() != null && network.insertFluid(result.getValue(), result.getValue().amount, Action.SIMULATE) == null) {
            network.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(toInsert, false);

            network.insertFluidTracked(result.getValue(), result.getValue().amount);

            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);

            ItemStack container = result.getLeft();
            if (!player.inventory.addItemStackToInventory(container.copy())) {
                InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), container);
            }
        }
    }

    public void extract(EntityPlayer player, EnumFacing side) {
        if (network == null || getDirection() != side) {
            return;
        }

        if (!network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }
        if (getType() == IType.ITEMS) {
            extractItems(player);
        } else if (getType() == IType.FLUIDS) {
            extractFluids(player);
        }
    }

    private void extractFluids(EntityPlayer player) {

        FluidStack filter = fluidFilter.getFluid(0);

        if (filter == null || filter.amount == 0) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getList().get(filter);
        if (stack == null || stack.amount < Fluid.BUCKET_VOLUME) {
            return;
        }

        boolean shift = player.isSneaking();
        if (StackUtils.hasFluidBucket(stack) && shift) {
            ItemStack bucket = null;

            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack slot = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                    bucket = StackUtils.EMPTY_BUCKET.copy();

                    player.inventory.decrStackSize(i, 1);

                    break;
                }
            }

            if (bucket == null) {
                bucket = network.extractItem(StackUtils.EMPTY_BUCKET, 1, Action.PERFORM);
            }

            if (bucket != null) {
                IFluidHandlerItem fluidHandler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                network.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(network.extractFluid(stack, Fluid.BUCKET_VOLUME, Action.PERFORM), true);

                if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                    InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), fluidHandler.getContainer());
                }
            }
        }
    }

    private void extractItems(EntityPlayer player) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        int toExtract = player.isSneaking() ? 1 : 64;

        if (!filter.isEmpty()) {
            ItemStack result = network.extractItem(filter, toExtract, compare, Action.PERFORM);

            if (result != null) {
                if (!player.inventory.addItemStackToInventory(result.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), result);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        WorldUtils.updateBlock(world, pos);

        markDirty();
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);

        StackUtils.writeItems(itemFilter, 0, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilter.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }
        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        StackUtils.readItems(itemFilter, 0, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilter.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
        }
    }

    public int getAmount() {
        if (network == null) {
            return 0;
        }

        if (getType() == IType.ITEMS) {
            ItemStack toCheck = itemFilter.getStackInSlot(0);

            if (toCheck.isEmpty()) {
                return 0;
            }

            ItemStack stored = network.getItemStorageCache().getList().get(toCheck, compare);

            return stored != null ? stored.getCount() : 0;
        } else if (getType() == IType.FLUIDS) {
            FluidStack toCheck = fluidFilter.getFluid(0);
            if (toCheck == null) {
                return 0;
            }

            FluidStack stored = network.getFluidStorageCache().getList().get(toCheck, compare);
            return stored != null ? stored.amount : 0;
        }
        return 0;
    }

    @Override
    public int getType() {
        return world.isRemote ? TileStorageMonitor.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    public ItemHandlerBase getItemFilters() {
        return itemFilter;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilter;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
