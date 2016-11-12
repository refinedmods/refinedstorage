package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ReaderWriterHandlerItems implements IReaderWriterHandler {
    public static final String ID = "items";

    private ItemStackHandler internalInv;

    public ReaderWriterHandlerItems(@Nullable NBTTagCompound tag) {
        this.internalInv = new ItemHandlerReaderWriter(4);

        if (tag != null) {
            RSUtils.readItems(internalInv, 0, tag);
        }
    }

    @Override
    public void update(IReaderWriterChannel channel) {
        for (IWriter writer : channel.getWriters()) {
            IItemHandler handler = RSUtils.getItemHandler(writer.getNodeWorld().getTileEntity(writer.getPosition().offset(writer.getDirection())), writer.getDirection().getOpposite());

            if (handler == null) {
                continue;
            }

            for (int i = 0; i < internalInv.getSlots(); ++i) {
                ItemStack slot = internalInv.getStackInSlot(i);

                if (slot == null) {
                    continue;
                }

                ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(slot, 1);

                if (ItemHandlerHelper.insertItem(handler, toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(handler, toInsert, false);

                    internalInv.getStackInSlot(i).stackSize -= toInsert.stackSize;

                    if (internalInv.getStackInSlot(i).stackSize <= 0) {
                        internalInv.setStackInSlot(i, null);
                    }
                }
            }
        }
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        // NO OP
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        RSUtils.writeItems(internalInv, 0, tag);

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return (T) internalInv;
    }

    private class ItemHandlerReaderWriter extends ItemStackHandler {
        private ItemHandlerReaderWriter(int size) {
            super(size);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return null;
        }
    }
}
