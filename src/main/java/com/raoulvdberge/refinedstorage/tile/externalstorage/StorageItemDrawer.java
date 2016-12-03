package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class StorageItemDrawer extends StorageItemExternal {
    private TileExternalStorage externalStorage;
    private IDrawer drawer;

    public StorageItemDrawer(TileExternalStorage externalStorage, IDrawer drawer) {
        this.externalStorage = externalStorage;
        this.drawer = drawer;
    }

    @Override
    public int getCapacity() {
        return drawer.getMaxCapacity();
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        return getStacks(drawer);
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        return insert(externalStorage, drawer, stack, size, simulate);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        return extract(drawer, stack, size, flags, simulate);
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

    public static NonNullList<ItemStack> getStacks(IDrawer drawer) {
        if (!drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return NonNullList.withSize(1, ItemHandlerHelper.copyStackWithSize(drawer.getStoredItemPrototype(), drawer.getStoredItemCount()));
        }

        return RSUtils.emptyNonNullList();
    }

    public static ItemStack insert(TileExternalStorage externalStorage, IDrawer drawer, @Nonnull ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            int stored = drawer.getStoredItemCount();
            int remainingSpace = drawer.getMaxCapacity(stack) - stored;

            int inserted = remainingSpace > size ? size : (remainingSpace <= 0) ? 0 : remainingSpace;

            if (!simulate && remainingSpace > 0) {
                if (drawer.isEmpty()) {
                    drawer.setStoredItem(stack, inserted);
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

    public static ItemStack extract(IDrawer drawer, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
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
