package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemStorageDrawerGroup extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private IDrawerGroup drawers;

    public ItemStorageDrawerGroup(TileExternalStorage externalStorage, IDrawerGroup drawers) {
        this.externalStorage = externalStorage;
        this.drawers = drawers;
    }

    @Override
    public List<ItemStack> getStacks() {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < drawers.getDrawerCount(); ++i) {
            if (drawers.isDrawerEnabled(i)) {
                stacks.addAll(ItemStorageDrawer.getStacks(drawers.getDrawer(i)));
            }
        }

        return stacks;
    }

    @Override
    public int getStored() {
        int stored = 0;

        for (int i = 0; i < drawers.getDrawerCount(); ++i) {
            if (drawers.isDrawerEnabled(i)) {
                stored += drawers.getDrawer(i).getStoredItemCount();
            }
        }

        return stored;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (int i = 0; i < drawers.getDrawerCount(); ++i) {
            if (drawers.isDrawerEnabled(i)) {
                capacity += drawers.getDrawer(i).getMaxCapacity();
            }
        }

        return capacity;
    }

    @Nullable
    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack remainder = stack;

        for (int i = 0; i < drawers.getDrawerCount(); ++i) {
            if (drawers.isDrawerEnabled(i)) {
                remainder = ItemStorageDrawer.insertItem(externalStorage, drawers.getDrawer(i), stack, size, simulate);

                if (remainder == null || remainder.stackSize <= 0) {
                    break;
                } else {
                    size = remainder.stackSize;
                }
            }
        }

        return remainder;
    }

    @Nullable
    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        int toExtract = size;

        ItemStack result = null;

        for (int i = 0; i < drawers.getDrawerCount(); ++i) {
            if (drawers.isDrawerEnabled(i)) {
                ItemStack extracted = ItemStorageDrawer.extractItem(drawers.getDrawer(i), stack, toExtract, flags, simulate);

                if (extracted != null) {
                    if (result == null) {
                        result = extracted;
                    } else {
                        result.stackSize += extracted.stackSize;
                    }

                    toExtract -= extracted.stackSize;
                }

                if (toExtract == 0) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }
}
