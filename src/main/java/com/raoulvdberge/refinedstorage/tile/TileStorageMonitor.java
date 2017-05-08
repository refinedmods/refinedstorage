package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class TileStorageMonitor extends TileNode<NetworkNodeStorageMonitor> {
    private static final String NBT_TYPE = "Type";
    private static final String NBT_STACK = "Stack";
    private static final String NBT_AMOUNT = "Amount";

    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private int type;
    private int amount;
    @Nullable
    private ItemStack itemStack;

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

        ItemStack stack = getNode().getType() == IType.ITEMS ? getNode().getItemFilter().getStackInSlot(0) : getNode().getFluidFilter().getStackInSlot(0);

        if (!stack.isEmpty()) {
            tag.setTag(NBT_STACK, stack.writeToNBT(new NBTTagCompound()));
        }

        tag.setInteger(NBT_AMOUNT, getNode().getAmount());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        type = tag.getInteger(NBT_TYPE);
        itemStack = tag.hasKey(NBT_STACK) ? new ItemStack(tag.getCompoundTag(NBT_STACK)) : null;
        amount = tag.getInteger(NBT_AMOUNT);
    }

    @Override
    protected boolean canCauseRenderUpdate(NBTTagCompound tag) {
        EnumFacing receivedDirection = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));
        boolean receivedActive = tag.getBoolean(NBT_ACTIVE);

        return receivedDirection != getDirection() || receivedActive != getNode().isActive();
    }

    public int getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }
}
