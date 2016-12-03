package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReaderWriterHandlerItems implements IReaderWriterHandler {
    public static final String ID = "items";

    private ItemStackHandler items;
    private ItemHandlerReaderWriter itemsReader, itemsWriter;

    public ReaderWriterHandlerItems(@Nullable NBTTagCompound tag) {
        this.items = new ItemStackHandler(4);
        this.itemsWriter = new ItemHandlerReaderWriter(items, false, true);
        this.itemsReader = new ItemHandlerReaderWriter(items, true, false);

        if (tag != null) {
            RSUtils.readItems(items, 0, tag);
        }
    }

    @Override
    public void update(IReaderWriterChannel channel) {
        // NO OP
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        // NO OP
    }

    @Override
    public boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (readerWriter instanceof IReader || readerWriter instanceof IWriter);
    }

    @Override
    public <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (readerWriter instanceof IReader) {
                return (T) itemsReader;
            } else if (readerWriter instanceof IWriter) {
                return (T) itemsWriter;
            }
        }

        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        RSUtils.writeItems(items, 0, tag);

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    private class ItemHandlerReaderWriter implements IItemHandler {
        private IItemHandler parent;
        private boolean canInsert, canExtract;

        public ItemHandlerReaderWriter(IItemHandler parent, boolean canInsert, boolean canExtract) {
            this.parent = parent;
            this.canInsert = canInsert;
            this.canExtract = canExtract;
        }

        @Override
        public int getSlots() {
            return parent.getSlots();
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot) {
            return parent.getStackInSlot(slot);
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return canInsert ? parent.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return canExtract ? parent.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return parent.getSlotLimit(slot);
        }
    }
}
