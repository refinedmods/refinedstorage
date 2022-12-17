package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
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

    private final BaseItemHandler itemFilter = new BaseItemHandler(1)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                LevelUtils.updateBlock(level, pos);
            }
        });

    private final FluidInventory fluidFilter = new FluidInventory(1, FluidType.BUCKET_VOLUME)
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                LevelUtils.updateBlock(level, pos);
            }
        });
    private final Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;

    private int oldAmount = -1;

    public StorageMonitorNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate()) {
            return;
        }

        int newAmount = getAmount();

        if (oldAmount == -1) {
            oldAmount = newAmount;
        } else if (oldAmount != newAmount) {
            oldAmount = newAmount;

            LevelUtils.updateBlock(level, pos);
        }
    }

    public InteractionResult depositAll(Player player) {
        if (getType() != IType.ITEMS) {
            return InteractionResult.FAIL;
        }

        if (network == null) {
            return InteractionResult.FAIL;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return InteractionResult.FAIL;
        }

        Pair<ItemStack, Long> deposit = deposits.get(player.getGameProfile().getName());

        if (deposit == null) {
            return InteractionResult.FAIL;
        }

        ItemStack inserted = deposit.getKey();
        long insertedAt = deposit.getValue();

        if (System.currentTimeMillis() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack toInsert = player.getInventory().getItem(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.getInventory().setItem(i, network.insertItemTracked(toInsert, toInsert.getCount()));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    public InteractionResult deposit(Player player, ItemStack toInsert) {
        if (network == null) {
            return InteractionResult.FAIL;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return InteractionResult.FAIL;
        }

        if (getType() == IType.ITEMS) {
            depositItems(player, toInsert);
        } else if (getType() == IType.FLUIDS) {
            depositFluids(player, toInsert);
        }

        return InteractionResult.SUCCESS;
    }

    private void depositItems(Player player, ItemStack toInsert) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        if (!filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.getInventory().setItem(player.getInventory().selected, network.insertItemTracked(toInsert, toInsert.getCount()));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, System.currentTimeMillis()));
        }
    }

    private void depositFluids(Player player, ItemStack toInsert) {
        FluidStack filter = fluidFilter.getFluid(0);

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(toInsert, true);

        if (filter.isEmpty() || !API.instance().getComparer().isEqual(filter, result.getRight(), compare)) {
            return;
        }

        if (!result.getValue().isEmpty() && network.insertFluid(result.getValue(), result.getValue().getAmount(), Action.SIMULATE).isEmpty()) {
            network.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(toInsert, false);

            network.insertFluidTracked(result.getValue(), result.getValue().getAmount());

            player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);

            ItemStack container = result.getLeft();
            if (!player.getInventory().add(container.copy())) {
                Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), container);
            }
        }
    }

    public void extract(Player player, Direction side) {
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

    private void extractItems(Player player) {
        ItemStack filter = itemFilter.getStackInSlot(0);

        int toExtract = player.isCrouching() ? 1 : filter.getMaxStackSize();

        if (!filter.isEmpty()) {
            ItemStack result = network.extractItem(filter, toExtract, compare, Action.PERFORM);

            if (!result.isEmpty() && !player.getInventory().add(result.copy())) {
                Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), result);
            }
        }
    }

    private void extractFluids(Player player) {
        FluidStack filter = fluidFilter.getFluid(0);

        if (filter.isEmpty()) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getList().get(filter);
        if (stack == null || stack.getAmount() < FluidType.BUCKET_VOLUME) {
            return;
        }

        boolean shift = player.isCrouching();
        if (shift) {
            NetworkUtils.extractBucketFromPlayerInventoryOrNetwork(player, network, bucket -> bucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).ifPresent(fluidHandler -> {
                network.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(network.extractFluid(stack, FluidType.BUCKET_VOLUME, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE);

                if (!player.getInventory().add(fluidHandler.getContainer().copy())) {
                    Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), fluidHandler.getContainer());
                }
            }));
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

        LevelUtils.updateBlock(level, pos);

        markDirty();
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilter, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilter.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
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

            if (compare == IComparer.COMPARE_NBT) {
                ItemStack stored = network.getItemStorageCache().getList().get(toCheck, compare);
                return stored != null ? stored.getCount() : 0;
            } else {
                return network.getItemStorageCache().getList().getStacks(toCheck).stream().mapToInt(entry -> entry.getStack().getCount()).sum();
            }

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
        return level.isClientSide ? StorageMonitorBlockEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        LevelUtils.updateBlock(level, pos);
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
