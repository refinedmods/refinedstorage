package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public class StorageItemDrawer extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IDrawer> drawerSupplier;

    public StorageItemDrawer(NetworkNodeExternalStorage externalStorage, Supplier<IDrawer> drawerSupplier) {
        this.externalStorage = externalStorage;
        this.drawerSupplier = drawerSupplier;
    }

    @Override
    public int getCapacity() {
        IDrawer drawer = drawerSupplier.get();

        return drawer != null ? drawer.getMaxCapacity() : 0;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return Collections.singletonList(getStack(drawerSupplier.get()));
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        return insert(externalStorage, drawerSupplier.get(), stack, size, simulate);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        return extract(drawerSupplier.get(), stack, size, flags, simulate);
    }

    @Override
    public int getStored() {
        IDrawer drawer = drawerSupplier.get();

        return drawer != null ? drawer.getStoredItemCount() : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    public static ItemStack getStack(@Nullable IDrawer drawer) {
        if (drawer != null && !drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return ItemHandlerHelper.copyStackWithSize(drawer.getStoredItemPrototype(), drawer.getStoredItemCount());
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack insert(NetworkNodeExternalStorage externalStorage, @Nullable IDrawer drawer, @Nonnull ItemStack stack, int size, boolean simulate) {
        if (drawer != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            int remainder = simulate ? Math.max(size - drawer.getAcceptingRemainingCapacity(), 0) : drawer.adjustStoredItemCount(size);

            return remainder == 0 ? null : ItemHandlerHelper.copyStackWithSize(stack, remainder);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    public static ItemStack extract(@Nullable IDrawer drawer, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        if (drawer != null && API.instance().getComparer().isEqual(stack, drawer.getStoredItemPrototype(), flags)) {
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
