package com.raoulvdberge.refinedstorage.apiimpl.network.node.storage;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageDiskFluidStorageWrapper implements IStorageDisk<FluidStack> {
    private NetworkNodeFluidStorage storage;
    private IStorageDisk<FluidStack> parent;

    public StorageDiskFluidStorageWrapper(NetworkNodeFluidStorage storage, IStorageDisk<FluidStack> parent) {
        this.storage = storage;
        this.parent = parent;
        this.setSettings(null, storage);
    }

    @Override
    public int getPriority() {
        return storage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return parent.getAccessType();
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return parent.getStacks();
    }

    @Override
    @Nullable
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        if (!IFilterable.acceptsFluid(storage.getFilters(), storage.getMode(), storage.getCompare(), stack)) {
            return StackUtils.copy(stack, size);
        }

        return parent.insert(stack, size, action);
    }

    @Nullable
    @Override
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        return parent.extract(stack, size, flags, action);
    }

    @Override
    public int getStored() {
        return parent.getStored();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        return parent.getCacheDelta(storedPreInsertion, size, remainder);
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
    public NBTTagCompound writeToNbt() {
        return parent.writeToNbt();
    }

    @Override
    public String getId() {
        return parent.getId();
    }
}