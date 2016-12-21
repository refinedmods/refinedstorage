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

public class StorageItemDSU extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private IDeepStorageUnit unit;

    public StorageItemDSU(NetworkNodeExternalStorage externalStorage, IDeepStorageUnit unit) {
        this.externalStorage = externalStorage;
        this.unit = unit;
    }

    @Override
    public int getCapacity() {
        return unit.getMaxStoredCount();
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        if (unit.getStoredItemType() != null && unit.getStoredItemType().getCount() > 0) {
            return NonNullList.withSize(1, unit.getStoredItemType().copy());
        }

        return RSUtils.emptyNonNullList();
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            if (unit.getStoredItemType() != null) {
                if (API.instance().getComparer().isEqualNoQuantity(unit.getStoredItemType(), stack)) {
                    if (getStored() + size > unit.getMaxStoredCount()) {
                        int remainingSpace = getCapacity() - getStored();

                        if (remainingSpace <= 0) {
                            return ItemHandlerHelper.copyStackWithSize(stack, size);
                        }

                        if (!simulate) {
                            unit.setStoredItemCount(unit.getStoredItemType().getCount() + remainingSpace);
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                    } else {
                        if (!simulate) {
                            unit.setStoredItemCount(unit.getStoredItemType().getCount() + size);
                        }

                        return null;
                    }
                }
            } else {
                if (getStored() + size > unit.getMaxStoredCount()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        unit.setStoredItemType(stack.copy(), remainingSpace);
                    }

                    return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        unit.setStoredItemType(stack.copy(), size);
                    }

                    return null;
                }
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        if (API.instance().getComparer().isEqual(stack, unit.getStoredItemType(), flags)) {
            if (size > unit.getStoredItemType().getCount()) {
                size = unit.getStoredItemType().getCount();
            }

            ItemStack stored = unit.getStoredItemType();

            if (!simulate) {
                unit.setStoredItemCount(stored.getCount() - size);
            }

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }

    @Override
    public int getStored() {
        return unit.getStoredItemType() != null ? unit.getStoredItemType().getCount() : 0;
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
