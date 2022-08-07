package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class StorageMonitorBlockEntity extends NetworkNodeBlockEntity<StorageMonitorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, StorageMonitorBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, StorageMonitorBlockEntity> TYPE = IType.createParameter();

    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUIDSTACK = "FluidStack";
    private static final String NBT_STACK = "Stack";
    private static final String NBT_AMOUNT = "Amount";

    private int type;
    private int amount;
    @Nullable
    private ItemStack itemStack = ItemStack.EMPTY;
    @Nullable
    private FluidStack fluidStack = FluidStack.EMPTY;

    public StorageMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.STORAGE_MONITOR.get(), pos, state);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public StorageMonitorNetworkNode createNode(Level level, BlockPos pos) {
        return new StorageMonitorNetworkNode(level, pos);
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        ItemStack stack = getNode().getItemFilters().getStackInSlot(0);

        if (!stack.isEmpty()) {
            tag.put(NBT_STACK, stack.save(new CompoundTag()));
        }

        FluidStack fluid = getNode().getFluidFilters().getFluid(0);
        if (!fluid.isEmpty()) {
            tag.put(NBT_FLUIDSTACK, fluid.writeToNBT(new CompoundTag()));
        }

        tag.putInt(NBT_TYPE, getNode().getType());
        tag.putInt(NBT_AMOUNT, getNode().getAmount());

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);
        fluidStack = tag.contains(NBT_FLUIDSTACK) ? FluidStack.loadFluidStackFromNBT(tag.getCompound(NBT_FLUIDSTACK)) : FluidStack.EMPTY;
        itemStack = tag.contains(NBT_STACK) ? ItemStack.of(tag.getCompound(NBT_STACK)) : ItemStack.EMPTY;
        type = tag.contains(NBT_TYPE) ? tag.getInt(NBT_TYPE) : IType.ITEMS;
        amount = tag.getInt(NBT_AMOUNT);
    }

    public int getAmount() {
        return amount;
    }

    public int getStackType() {
        return type;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public FluidStack getFluidStack() {
        return fluidStack;
    }
}
