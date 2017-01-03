package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemStorageDSU extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private Supplier<IDeepStorageUnit> dsuSupplier;

    public ItemStorageDSU(TileExternalStorage externalStorage, Supplier<IDeepStorageUnit> dsuSupplier) {
        this.externalStorage = externalStorage;
        this.dsuSupplier = dsuSupplier;
    }

    @Override
    public int getCapacity() {
        IDeepStorageUnit dsu = dsuSupplier.get();

        return dsu != null ? dsu.getMaxStoredCount() : 0;
    }

    @Override
    public List<ItemStack> getStacks() {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (dsu != null && dsu.getStoredItemType() != null && dsu.getStoredItemType().stackSize > 0) {
            return Collections.singletonList(dsu.getStoredItemType().copy());
        }

        return Collections.emptyList();
    }

    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (dsu != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            if (dsu.getStoredItemType() != null) {
                if (API.instance().getComparer().isEqualNoQuantity(dsu.getStoredItemType(), stack)) {
                    if (getStored() + size > dsu.getMaxStoredCount()) {
                        int remainingSpace = getCapacity() - getStored();

                        if (remainingSpace <= 0) {
                            return ItemHandlerHelper.copyStackWithSize(stack, size);
                        }

                        if (!simulate) {
                            dsu.setStoredItemCount(dsu.getStoredItemType().stackSize + remainingSpace);
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                    } else {
                        if (!simulate) {
                            dsu.setStoredItemCount(dsu.getStoredItemType().stackSize + size);
                        }

                        return null;
                    }
                }
            } else {
                if (getStored() + size > dsu.getMaxStoredCount()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        dsu.setStoredItemType(stack.copy(), remainingSpace);
                    }

                    return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        dsu.setStoredItemType(stack.copy(), size);
                    }

                    return null;
                }
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (dsu != null && API.instance().getComparer().isEqual(stack, dsu.getStoredItemType(), flags)) {
            if (size > dsu.getStoredItemType().stackSize) {
                size = dsu.getStoredItemType().stackSize;
            }

            ItemStack stored = dsu.getStoredItemType();

            if (!simulate) {
                dsu.setStoredItemCount(stored.stackSize - size);
            }

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }

    @Override
    public int getStored() {
        IDeepStorageUnit dsu = dsuSupplier.get();

        return (dsu != null && dsu.getStoredItemType() != null) ? dsu.getStoredItemType().stackSize : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }
}
