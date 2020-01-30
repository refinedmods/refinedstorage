package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.raoulvdberge.refinedstorage.tile.StorageMonitorTile;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class StorageMonitorNetworkNode extends NetworkNode implements IComparable, IType {
    public static final int DEPOSIT_ALL_MAX_DELAY = 500;

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "storage_monitor");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private BaseItemHandler itemFilter = new BaseItemHandler(1)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                WorldUtils.updateBlock(world, pos);
            }
        });

    private FluidInventory fluidFilter = new FluidInventory(1, FluidAttributes.BUCKET_VOLUME)
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                WorldUtils.updateBlock(world, pos);
            }
        });
    private Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;

    private int oldAmount = -1;

    public StorageMonitorNetworkNode(World world, BlockPos pos) {
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

    public ActionResultType depositAll(PlayerEntity player) {
        if (getType() != IType.ITEMS) {
            return ActionResultType.FAIL;
        }

        if (network == null) {
            return ActionResultType.FAIL;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return ActionResultType.FAIL;
        }

        Pair<ItemStack, Long> deposit = deposits.get(player.getGameProfile().getName());

        if (deposit == null) {
            return ActionResultType.FAIL;
        }

        ItemStack inserted = deposit.getKey();
        long insertedAt = deposit.getValue();

        if (System.currentTimeMillis() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack toInsert = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.inventory.setInventorySlotContents(i, network.insertItemTracked(toInsert, toInsert.getCount()));
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    public ActionResultType deposit(PlayerEntity player, ItemStack toInsert) {
        if (network == null) {
            return ActionResultType.FAIL;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return ActionResultType.FAIL;
        }

        if (getType() == IType.ITEMS) {
            depositItems(player, toInsert);
        } else if (getType() == IType.FLUIDS) {
            depositFluids(player, toInsert);
        }

        return ActionResultType.SUCCESS;
    }

    private void depositItems(PlayerEntity player, ItemStack toInsert) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        if (!filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, network.insertItemTracked(toInsert, toInsert.getCount()));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, System.currentTimeMillis()));
        }
    }

    private void depositFluids(PlayerEntity player, ItemStack toInsert) {
        FluidStack filter = fluidFilter.getFluid(0);

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(toInsert, true);

        if (filter.isEmpty() || !API.instance().getComparer().isEqual(filter, result.getRight(), compare)) {
            return;
        }

        if (!result.getValue().isEmpty() && network.insertFluid(result.getValue(), result.getValue().getAmount(), Action.SIMULATE).isEmpty()) {
            network.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(toInsert, false);

            network.insertFluidTracked(result.getValue(), result.getValue().getAmount());

            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);

            ItemStack container = result.getLeft();
            if (!player.inventory.addItemStackToInventory(container.copy())) {
                InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), container);
            }
        }
    }

    public void extract(PlayerEntity player, Direction side) {
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

    private void extractItems(PlayerEntity player) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        int toExtract = player.isCrouching() ? 1 : 64;

        if (!filter.isEmpty()) {
            ItemStack result = network.extractItem(filter, toExtract, compare, Action.PERFORM);

            if (!result.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(result.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), result);
                }
            }
        }
    }

    private void extractFluids(PlayerEntity player) {
        FluidStack filter = fluidFilter.getFluid(0);

        if (filter.isEmpty()) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getList().get(filter);
        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
            return;
        }

        boolean shift = player.isCrouching();
        if (shift) {
            NetworkUtils.extractBucketFromPlayerInventoryOrNetwork(player, network, bucket -> {
                bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fluidHandler -> {
                    network.getFluidStorageTracker().changed(player, stack.copy());

                    fluidHandler.fill(network.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE);

                    if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), fluidHandler.getContainer());
                    }
                });
            });
        }
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getStorageMonitor().getUsage();
    }

    @Override
    public ResourceLocation getId() {
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
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilter, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilter.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        StackUtils.readItems(itemFilter, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilter.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
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

            if (toCheck.isEmpty()) {
                return 0;
            }

            FluidStack stored = network.getFluidStorageCache().getList().get(toCheck, compare);

            return stored != null ? stored.getAmount() : 0;
        }
        return 0;
    }

    @Override
    public int getType() {
        return world.isRemote ? StorageMonitorTile.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        WorldUtils.updateBlock(world, pos);
        markDirty();
    }

    public BaseItemHandler getItemFilters() {
        return itemFilter;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilter;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
