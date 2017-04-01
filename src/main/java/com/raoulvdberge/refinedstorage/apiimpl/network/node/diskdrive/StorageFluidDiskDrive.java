package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageFluidDiskDrive implements IStorageDisk<FluidStack> {
    private NetworkNodeDiskDrive diskDrive;
    private IStorageDisk<FluidStack> parent;
    private int lastState;

    public StorageFluidDiskDrive(NetworkNodeDiskDrive diskDrive, IStorageDisk<FluidStack> parent) {
        this.diskDrive = diskDrive;
        this.parent = parent;
        this.parent.setListener(() -> {
            diskDrive.markDirty();

            int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

            if (lastState != currentState) {
                lastState = currentState;

                RSUtils.updateBlock(diskDrive.getHolder().world(), diskDrive.getHolder().pos());
            }
        });
        this.lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
    }

    @Override
    public int getPriority() {
        return diskDrive.getPriority();
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return parent.getStacks();
    }

    @Override
    @Nullable
    public FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (!IFilterable.canTakeFluids(diskDrive.getFluidFilters(), diskDrive.getMode(), diskDrive.getCompare(), stack)) {
            return RSUtils.copyStackWithSize(stack, size);
        }

        return parent.insert(stack, size, simulate);
    }

    @Nullable
    @Override
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        return parent.extract(stack, size, flags, simulate);
    }

    @Override
    public int getStored() {
        return parent.getStored();
    }

    @Override
    public AccessType getAccessType() {
        return diskDrive.getAccessType();
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
    public boolean isVoiding() {
        return diskDrive.getVoidExcess();
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return parent.isValid(stack);
    }

    @Override
    public void setListener(Runnable listener) {
        // NO OP
    }

    @Override
    public void readFromNBT() {
        parent.readFromNBT();
    }

    @Override
    public void writeToNBT() {
        parent.writeToNBT();
    }

    @Override
    public StorageDiskType getType() {
        return parent.getType();
    }
}