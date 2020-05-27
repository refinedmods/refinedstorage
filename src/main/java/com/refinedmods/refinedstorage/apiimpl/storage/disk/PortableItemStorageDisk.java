package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class PortableItemStorageDisk implements IStorageDisk<ItemStack> {
    private IStorageDisk<ItemStack> parent;
    private IPortableGrid portableGrid;

    public PortableItemStorageDisk(IStorageDisk<ItemStack> parent, IPortableGrid portableGrid) {
        this.parent = parent;
        this.portableGrid = portableGrid;
    }

    @Override
    public int getCapacity() {
        return parent.getCapacity();
    }

    @Override
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        parent.setSettings(listener, context);
    }

    @Override
    public CompoundNBT writeToNbt() {
        return parent.writeToNbt();
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return parent.getStacks();
    }

    @Override
    @Nonnull
    public ItemStack insert(@Nonnull ItemStack stack, int size, Action action) {
        int storedPre = parent.getStored();

        ItemStack remainder = parent.insert(stack, size, action);

        if (action == Action.PERFORM) {
            int inserted = parent.getCacheDelta(storedPre, size, remainder);

            if (inserted > 0) {
                portableGrid.getItemCache().add(stack, inserted, false, false);
            }
        }

        return remainder;
    }

    @Override
    @Nonnull
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, Action action) {
        ItemStack extracted = parent.extract(stack, size, flags, action);

        if (action == Action.PERFORM && !extracted.isEmpty()) {
            portableGrid.getItemCache().remove(extracted, extracted.getCount(), false);
        }

        return extracted;
    }

    @Override
    public int getStored() {
        return parent.getStored();
    }

    @Override
    public int getPriority() {
        return parent.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return parent.getAccessType();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        return parent.getCacheDelta(storedPreInsertion, size, remainder);
    }

    @Override
    public ResourceLocation getFactoryId() {
        return parent.getFactoryId();
    }
}
