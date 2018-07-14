package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReaderWriterHandlerItems implements IReaderWriterHandler {
    public static final String ID = "items";

    private static final IItemHandler NULL_CAP = new IItemHandler() {
        @Override
        public int getSlots() {
            return 0;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }
    };

    private ItemStackHandler items;
    private ItemHandlerReaderWriter itemsReader, itemsWriter;

    public ReaderWriterHandlerItems(@Nullable NBTTagCompound tag) {
        this.items = new ItemStackHandler(16);
        this.itemsWriter = new ItemHandlerReaderWriter(items, false, true);
        this.itemsReader = new ItemHandlerReaderWriter(items, true, false);

        if (tag != null) {
            StackUtils.readItems(items, 0, tag);
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
    public boolean hasCapabilityReader(IReader reader, Capability<?> capability) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapabilityReader(IReader reader, Capability<T> capability) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemsReader);
        }

        return null;
    }

    @Override
    public boolean hasCapabilityWriter(IWriter writer, Capability<?> capability) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapabilityWriter(IWriter writer, Capability<T> capability) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemsWriter);
        }

        return null;
    }

    @Override
    public Object getNullCapability() {
        return NULL_CAP;
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        StackUtils.writeItems(items, 0, tag);

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<ITextComponent> getStatusReader(IReader reader, IReaderWriterChannel channel) {
        return getStatus(itemsReader);
    }

    @Override
    public List<ITextComponent> getStatusWriter(IWriter writer, IReaderWriterChannel channel) {
        return getStatus(itemsWriter);
    }

    private List<ITextComponent> getStatus(IItemHandler handler) {
        List<ITextComponent> components = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);

            if (!stack.isEmpty()) {
                components.add(new TextComponentString(stack.getCount() + "x ").appendSibling(new TextComponentTranslation(stack.getTranslationKey() + ".name")));
            }
        }

        return components;
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
