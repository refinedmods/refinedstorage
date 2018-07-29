package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageDiskFluidPortable implements IStorageDisk<FluidStack> {
    private IStorageDisk<FluidStack> parent;
    private IPortableGrid portableGrid;

    public StorageDiskFluidPortable(IStorageDisk<FluidStack> parent, IPortableGrid portableGrid) {
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
    public NBTTagCompound writeToNbt() {
        return parent.writeToNbt();
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return parent.getStacks();
    }

    @Nullable
    @Override
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        int storedPre = parent.getStored();

        FluidStack remainder = parent.insert(stack, size, action);

        if (action == Action.PERFORM) {
            int inserted = parent.getCacheDelta(storedPre, size, remainder);

            if (inserted > 0) {
                portableGrid.getFluidCache().add(stack, inserted, false, false);
            }
        }

        return remainder;
    }

    @Nullable
    @Override
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        FluidStack extracted = parent.extract(stack, size, flags, action);

        if (action == Action.PERFORM && extracted != null) {
            portableGrid.getFluidCache().remove(extracted, extracted.amount, false);
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
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        return parent.getCacheDelta(storedPreInsertion, size, remainder);
    }

    @Override
    public String getId() {
        return parent.getId();
    }
}
