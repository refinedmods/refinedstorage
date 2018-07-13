package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsDisk;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageDiskFluidManipulatorWrapper implements IStorageDisk<FluidStack> {
    private NetworkNodeDiskManipulator diskManipulator;
    private IStorageDisk<FluidStack> parent;
    private int lastState;

    public StorageDiskFluidManipulatorWrapper(NetworkNodeDiskManipulator diskManipulator, IStorageDisk<FluidStack> parent) {
        this.diskManipulator = diskManipulator;
        this.parent = parent;
        this.setSettings(
            () -> {
                int currentState = ConstantsDisk.getDiskState(getStored(), getCapacity());

                if (lastState != currentState) {
                    lastState = currentState;

                    WorldUtils.updateBlock(diskManipulator.getWorld(), diskManipulator.getPos());
                }
            },
            diskManipulator
        );
        this.lastState = ConstantsDisk.getDiskState(getStored(), getCapacity());
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

    @Override
    public Collection<FluidStack> getStacks() {
        return parent.getStacks();
    }

    @Override
    @Nullable
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        if (!IFilterable.acceptsFluid(diskManipulator.getFluidFilters(), diskManipulator.getMode(), diskManipulator.getCompare(), stack)) {
            return StackUtils.copy(stack, size);
        }

        return parent.insert(stack, size, action);
    }

    @Override
    @Nullable
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        if (!IFilterable.acceptsFluid(diskManipulator.getFluidFilters(), diskManipulator.getMode(), diskManipulator.getCompare(), stack)) {
            return null;
        }

        return parent.extract(stack, size, flags, action);
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
}
