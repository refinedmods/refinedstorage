package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageDiskItemManipulatorWrapper implements IStorageDisk<ItemStack> {
    private NetworkNodeDiskManipulator diskManipulator;
    private IStorageDisk<ItemStack> parent;
    private int lastState;

    public StorageDiskItemManipulatorWrapper(NetworkNodeDiskManipulator diskManipulator, IStorageDisk<ItemStack> parent) {
        this.diskManipulator = diskManipulator;
        this.parent = parent;
        this.setSettings(
            () -> {
                int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

                if (lastState != currentState) {
                    lastState = currentState;

                    WorldUtils.updateBlock(diskManipulator.getWorld(), diskManipulator.getPos());
                }
            },
            diskManipulator
        );
        this.lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
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
    public Collection<ItemStack> getStacks() {
        return parent.getStacks();
    }

    @Override
    @Nullable
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        if (!IFilterable.canTake(diskManipulator.getItemFilters(), diskManipulator.getMode(), diskManipulator.getCompare(), stack)) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        return parent.insert(stack, size, simulate);
    }

    @Override
    @Nullable
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        if (!IFilterable.canTake(diskManipulator.getItemFilters(), diskManipulator.getMode(), diskManipulator.getCompare(), stack)) {
            return null;
        }

        return parent.extract(stack, size, flags, simulate);
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
    public String getId() {
        return parent.getId();
    }
}
