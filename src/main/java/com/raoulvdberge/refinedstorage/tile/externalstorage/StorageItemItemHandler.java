package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class StorageItemItemHandler extends StorageItemExternal {
    private TileExternalStorage externalStorage;
    private IItemHandler handler;
    private AccessType lockedAccessType = AccessType.INSERT_EXTRACT;

    public StorageItemItemHandler(TileExternalStorage externalStorage, IItemHandler handler) {
        this.externalStorage = externalStorage;
        this.handler = handler;

        if (externalStorage.getFacingTile().getBlockType().getUnlocalizedName().equals("tile.ExtraUtils2:TrashCan")) {
            lockedAccessType = AccessType.INSERT;
        }
    }

    @Override
    public int getCapacity() {
        return handler.getSlots() * 64;
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        NonNullList<ItemStack> stacks = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);

        for (int i = 0; i < handler.getSlots(); ++i) {
            stacks.set(i, handler.getStackInSlot(i).copy());
        }

        return stacks;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            return ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        int remaining = size;

        ItemStack received = null;

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, flags)) {
                ItemStack got = handler.extractItem(i, remaining, simulate);

                if (!got.isEmpty()) {
                    if (received == null) {
                        received = got;
                    } else {
                        received.grow(got.getCount());
                    }

                    remaining -= got.getCount();

                    if (remaining == 0) {
                        break;
                    }
                }
            }
        }

        return received;
    }

    @Override
    public int getStored() {
        int size = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                size += handler.getStackInSlot(i).getCount();
            }
        }

        return size;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return ((lockedAccessType != AccessType.INSERT_EXTRACT) ? lockedAccessType : externalStorage.getAccessType());
    }
}
