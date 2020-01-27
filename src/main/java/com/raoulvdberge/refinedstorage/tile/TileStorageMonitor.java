package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class TileStorageMonitor extends TileNode<NetworkNodeStorageMonitor> {
    public static final TileDataParameter<Integer, TileStorageMonitor> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TileStorageMonitor> COMPARE = IComparable.createParameter();

    private static final String NBT_TYPE = "Type";
    private static final String NBT_STACK = "Stack";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_FLUIDSTACK = "FluidStack";

    private int amount;
    private int type;
    @Nullable
    private ItemStack itemStack;
    @Nullable
    private FluidStack fluidStack;

    public TileStorageMonitor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public NetworkNodeStorageMonitor createNode(World world, BlockPos pos) {
        return new NetworkNodeStorageMonitor(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeStorageMonitor.ID;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        ItemStack stack = getNode().getItemFilters().getStackInSlot(0);

        if (!stack.isEmpty()) {
            tag.setTag(NBT_STACK, stack.writeToNBT(new NBTTagCompound()));
        }

        FluidStack fluid = getNode().getFluidFilters().getFluid(0);
        if (fluid != null) {
            tag.setTag(NBT_FLUIDSTACK, fluid.writeToNBT(new NBTTagCompound()));
        }

        tag.setInteger(NBT_AMOUNT, getNode().getAmount());
        tag.setInteger(NBT_TYPE, getNode().getType());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        itemStack = tag.hasKey(NBT_STACK) ? new ItemStack(tag.getCompoundTag(NBT_STACK)) : null;
        fluidStack = tag.hasKey(NBT_FLUIDSTACK) ? FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_FLUIDSTACK)) : null;
        amount = tag.hasKey(NBT_AMOUNT) ? tag.getInteger(NBT_AMOUNT) : 0;
        type = tag.hasKey(NBT_TYPE) ? tag.getInteger(NBT_TYPE) : IType.ITEMS;
    }

    @Override
    protected boolean canCauseRenderUpdate(NBTTagCompound tag) {
        EnumFacing receivedDirection = EnumFacing.byIndex(tag.getInteger(NBT_DIRECTION));
        boolean receivedActive = tag.getBoolean(NBT_ACTIVE);

        return receivedDirection != getDirection() || receivedActive != getNode().isActive();
    }

    public int getAmount() {
        return amount;
    }

    public int getType() {
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
