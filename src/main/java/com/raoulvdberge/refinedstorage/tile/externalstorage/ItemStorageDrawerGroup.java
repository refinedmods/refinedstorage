package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemStorageDrawerGroup extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private Supplier<IDrawerGroup> groupSupplier;

    public ItemStorageDrawerGroup(TileExternalStorage externalStorage, Supplier<IDrawerGroup> groupSupplier) {
        this.externalStorage = externalStorage;
        this.groupSupplier = groupSupplier;
    }

    @Override
    public List<ItemStack> getStacks() {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return Collections.emptyList();
        }

        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                stacks.addAll(ItemStorageDrawer.getStacks(group.getDrawer(i)));
            }
        }

        return stacks;
    }

    @Override
    public int getStored() {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return 0;
        }

        int stored = 0;

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                stored += group.getDrawer(i).getStoredItemCount();
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
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return 0;
        }

        int capacity = 0;

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                capacity += group.getDrawer(i).getMaxCapacity();
            }
        }

        return capacity;
    }

    @Nullable
    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return stack;
        }

        ItemStack remainder = stack;

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                remainder = ItemStorageDrawer.insertItem(externalStorage, group.getDrawer(i), stack, size, simulate);

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
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return null;
        }

        int toExtract = size;

        ItemStack result = null;

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                ItemStack extracted = ItemStorageDrawer.extractItem(group.getDrawer(i), stack, toExtract, flags, simulate);

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
