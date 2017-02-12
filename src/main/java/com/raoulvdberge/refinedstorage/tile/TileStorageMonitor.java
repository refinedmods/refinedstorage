package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

public class TileStorageMonitor extends TileNode<NetworkNodeStorageMonitor> {
    private static final String NBT_TYPE = "Type";
    private static final String NBT_STACK = "Stack";
    private static final String NBT_AMOUNT = "Amount";

    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private int type;
    private int amount;
    private ItemStack itemStack = ItemStack.EMPTY;
    private FluidStack fluidStack;

    public TileStorageMonitor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public NetworkNodeStorageMonitor createNode() {
        return new NetworkNodeStorageMonitor(this);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_TYPE, getNode().getType());
        tag.setTag(NBT_STACK, getNode().getType() == IType.ITEMS ? getNode().getItemFilter().getStackInSlot(0).writeToNBT(new NBTTagCompound()) : getNode().getFluidFilter().getFluidStackInSlot(0).writeToNBT(new NBTTagCompound()));
        tag.setInteger(NBT_AMOUNT, getNode().getAmount());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        type = tag.getInteger(NBT_TYPE);

        if (type == IType.ITEMS) {
            itemStack = new ItemStack(tag.getCompoundTag(NBT_STACK));
        } else {
            fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_STACK));
        }

        amount = tag.getInteger(NBT_AMOUNT);
    }

    @Override
    protected boolean canUpdateCauseRerender(NBTTagCompound tag) {
        EnumFacing receivedDirection = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));

        return receivedDirection != getDirection();
    }

    public int getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }
}
