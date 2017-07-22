package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDiskBehavior;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class StorageDiskBehavior implements IStorageDiskBehavior {
    @Override
    public IStorageDisk<ItemStack> createItemStorage(NBTTagCompound tag, int capacity) {
        return new StorageDiskItem(tag, capacity);
    }

    @Override
    public IStorageDisk<FluidStack> createFluidStorage(NBTTagCompound tag, int capacity) {
        return new StorageDiskFluid(tag, capacity);
    }

    @Override
    public NBTTagCompound getShareTag(StorageDiskType type, ItemStack stack) {
        switch (type) {
            case ITEMS:
                return StorageDiskItem.getShareTag(stack.getTagCompound());
            case FLUIDS:
                return StorageDiskFluid.getShareTag(stack.getTagCompound());
            default:
                throw new IllegalArgumentException("Expected items or fluids!");
        }
    }

    @Override
    public NBTTagCompound getTag(StorageDiskType type) {
        switch (type) {
            case ITEMS:
                return StorageDiskItem.getTag();
            case FLUIDS:
                return StorageDiskFluid.getTag();
            default:
                throw new IllegalArgumentException("Expected items or fluids!");
        }
    }
}
