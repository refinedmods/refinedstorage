package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StorageItemDrawerGroup extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IDrawerGroup> groupSupplier;

    public StorageItemDrawerGroup(NetworkNodeExternalStorage externalStorage, Supplier<IDrawerGroup> groupSupplier) {
        this.externalStorage = externalStorage;
        this.groupSupplier = groupSupplier;
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return RSUtils.emptyNonNullList();
        }

        NonNullList<ItemStack> stacks = NonNullList.create();

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            IDrawer drawer = group.getDrawer(i);

            if (group.isDrawerEnabled(i)) {
                stacks.addAll(StorageItemDrawer.getStacks(drawer));
            }
        }

        return stacks;
    }

    @Override
    public int getStored() {
        int stored = 0;

        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return 0;
        }

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            IDrawer drawer = group.getDrawer(i);

            if (group.isDrawerEnabled(i)) {
                stored += drawer.getStoredItemCount();
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

        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return 0;
        }

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                capacity += group.getDrawer(i).getMaxCapacity();
            }
        }

        return capacity;
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack remainder = stack;

        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return stack;
        }

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                remainder = StorageItemDrawer.insert(externalStorage, group.getDrawer(i), stack, size, simulate);

                if (remainder == null || remainder.getCount() <= 0) {
                    break;
                } else {
                    size = remainder.getCount();
                }
            }
        }

        return remainder;
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        int toExtract = size;

        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return null;
        }

        ItemStack result = null;

        for (int i = 0; i < group.getDrawerCount(); ++i) {
            if (group.isDrawerEnabled(i)) {
                ItemStack extracted = StorageItemDrawer.extract(group.getDrawer(i), stack, toExtract, flags, simulate);

                if (extracted != null) {
                    if (result == null) {
                        result = extracted;
                    } else {
                        result.grow(extracted.getCount());
                    }

                    toExtract -= extracted.getCount();
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
