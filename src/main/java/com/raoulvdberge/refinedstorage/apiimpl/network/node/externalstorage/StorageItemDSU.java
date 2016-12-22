package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class StorageItemDSU extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IDeepStorageUnit> dsuSupplier;

    public StorageItemDSU(NetworkNodeExternalStorage externalStorage, Supplier<IDeepStorageUnit> dsuSupplier) {
        this.externalStorage = externalStorage;
        this.dsuSupplier = dsuSupplier;
    }

    @Override
    public int getCapacity() {
        return dsuSupplier.get().getMaxStoredCount();
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (dsu.getStoredItemType() != null && dsu.getStoredItemType().getCount() > 0) {
            return NonNullList.withSize(1, dsu.getStoredItemType().copy());
        }

        return RSUtils.emptyNonNullList();
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            if (dsu.getStoredItemType() != null) {
                if (API.instance().getComparer().isEqualNoQuantity(dsu.getStoredItemType(), stack)) {
                    if (getStored() + size > dsu.getMaxStoredCount()) {
                        int remainingSpace = getCapacity() - getStored();

                        if (remainingSpace <= 0) {
                            return ItemHandlerHelper.copyStackWithSize(stack, size);
                        }

                        if (!simulate) {
                            dsu.setStoredItemCount(dsu.getStoredItemType().getCount() + remainingSpace);
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                    } else {
                        if (!simulate) {
                            dsu.setStoredItemCount(dsu.getStoredItemType().getCount() + size);
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
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        IDeepStorageUnit dsu = dsuSupplier.get();

        if (API.instance().getComparer().isEqual(stack, dsu.getStoredItemType(), flags)) {
            if (size > dsu.getStoredItemType().getCount()) {
                size = dsu.getStoredItemType().getCount();
            }

            ItemStack stored = dsu.getStoredItemType();

            if (!simulate) {
                dsu.setStoredItemCount(stored.getCount() - size);
            }

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }

    @Override
    public int getStored() {
        IDeepStorageUnit dsu = dsuSupplier.get();

        return dsu.getStoredItemType() != null ? dsu.getStoredItemType().getCount() : 0;
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
