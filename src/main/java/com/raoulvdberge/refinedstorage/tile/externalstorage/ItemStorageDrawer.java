package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Collections;
import java.util.List;

public class ItemStorageDrawer extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private IDrawer drawer;

    public ItemStorageDrawer(TileExternalStorage externalStorage, IDrawer drawer) {
        this.externalStorage = externalStorage;
        this.drawer = drawer;
    }

    @Override
    public int getCapacity() {
        return drawer.getMaxCapacity();
    }

    @Override
    public List<ItemStack> getStacks() {
        return getStacks(drawer);
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        return insertItem(externalStorage, drawer, stack, size, simulate);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags, boolean simulate) {
        return extractItem(drawer, stack, size, flags, simulate);
    }

    @Override
    public int getStored() {
        return drawer.getStoredItemCount();
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    public static List<ItemStack> getStacks(IDrawer drawer) {
        if (!drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return Collections.singletonList(drawer.getStoredItemCopy());
        }

        return Collections.emptyList();
    }

    public static ItemStack insertItem(TileExternalStorage externalStorage, IDrawer drawer, ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            int stored = drawer.getStoredItemCount();
            int remainingSpace = drawer.getMaxCapacity(stack) - stored;

            int inserted = remainingSpace > size ? size : (remainingSpace <= 0) ? 0 : remainingSpace;

            if (!simulate && remainingSpace > 0) {
                if (drawer.isEmpty()) {
                    drawer.setStoredItemRedir(stack, inserted);
                } else {
                    drawer.setStoredItemCount(stored + inserted);
                }
            }

            if (inserted == size) {
                return null;
            }

            int returnSize = size - inserted;

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) {
                returnSize = -returnSize;
            }

            return ItemHandlerHelper.copyStackWithSize(stack, returnSize);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    public static ItemStack extractItem(IDrawer drawer, ItemStack stack, int size, int flags, boolean simulate) {
        if (API.instance().getComparer().isEqual(stack, drawer.getStoredItemPrototype(), flags) && drawer.canItemBeExtracted(stack)) {
            if (size > drawer.getStoredItemCount()) {
                size = drawer.getStoredItemCount();
            }

            ItemStack stored = drawer.getStoredItemPrototype();

            if (!simulate) {
                drawer.setStoredItemCount(drawer.getStoredItemCount() - size);
            }

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }
}
