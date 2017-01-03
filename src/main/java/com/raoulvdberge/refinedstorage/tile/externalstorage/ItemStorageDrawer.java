package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemStorageDrawer extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private Supplier<IDrawer> drawerSupplier;

    public ItemStorageDrawer(TileExternalStorage externalStorage, Supplier<IDrawer> drawerSupplier) {
        this.externalStorage = externalStorage;
        this.drawerSupplier = drawerSupplier;
    }

    @Override
    public int getCapacity() {
        IDrawer drawer = drawerSupplier.get();

        return drawer != null ? drawer.getMaxCapacity() : 0;
    }

    @Override
    public List<ItemStack> getStacks() {
        return getStacks(drawerSupplier.get());
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        return insertItem(externalStorage, drawerSupplier.get(), stack, size, simulate);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags, boolean simulate) {
        return extractItem(drawerSupplier.get(), stack, size, flags, simulate);
    }

    @Override
    public int getStored() {
        return drawerSupplier.get().getStoredItemCount();
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    public static List<ItemStack> getStacks(@Nullable IDrawer drawer) {
        if (drawer != null && !drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return Collections.singletonList(drawer.getStoredItemCopy());
        }

        return Collections.emptyList();
    }

    public static boolean isVoidable(IDrawer drawer) {
        return drawer instanceof IVoidable && ((IVoidable) drawer).isVoid();
    }

    public static ItemStack insertItem(@Nullable TileExternalStorage externalStorage, IDrawer drawer, ItemStack stack, int size, boolean simulate) {
        if (drawer != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
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

            if (isVoidable(drawer)) {
                returnSize = -returnSize;
            }

            return ItemHandlerHelper.copyStackWithSize(stack, returnSize);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    public static ItemStack extractItem(@Nullable IDrawer drawer, ItemStack stack, int size, int flags, boolean simulate) {
        if (drawer != null && API.instance().getComparer().isEqual(stack, drawer.getStoredItemPrototype(), flags) && drawer.canItemBeExtracted(stack)) {
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
