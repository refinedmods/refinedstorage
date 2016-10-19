package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemStorageItemHandler extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private IItemHandler handler;
    private AccessType lockedAccessType = AccessType.READ_WRITE;

    public ItemStorageItemHandler(TileExternalStorage externalStorage, IItemHandler handler) {
        this.externalStorage = externalStorage;
        this.handler = handler;

        if (externalStorage.getFacingTile().getBlockType().getUnlocalizedName().equals("tile.ExtraUtils2:TrashCan")) {
            lockedAccessType = AccessType.WRITE;
        }
    }

    @Override
    public int getCapacity() {
        return handler.getSlots() * 64;
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getItem() != null) {
                items.add(handler.getStackInSlot(i).copy());
            }
        }

        return items;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            return ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        int remaining = size;

        ItemStack received = null;

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (slot != null && API.instance().getComparer().isEqual(slot, stack, flags)) {
                ItemStack got = handler.extractItem(i, remaining, false);

                if (got != null) {
                    if (received == null) {
                        received = got;
                    } else {
                        received.stackSize += got.stackSize;
                    }

                    remaining -= got.stackSize;

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
            if (handler.getStackInSlot(i) != null) {
                size += handler.getStackInSlot(i).stackSize;
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
        return ((lockedAccessType != AccessType.READ_WRITE) ? lockedAccessType : externalStorage.getAccessType());
    }
}
