package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * Helper class for creation and usage of basic storage disks.
 * For more advanced usage of storages, implement {@link IStorage} yourself.
 */
public interface IStorageDiskBehavior {
    /**
     * Creates an item storage for a disk.
     *
     * @param tag      the tag of the disk
     * @param capacity the capacity of the disk
     * @return the storage
     */
    IStorageDisk<ItemStack> createItemStorage(NBTTagCompound tag, int capacity);

    /**
     * Creates a fluid storage for a disk.
     *
     * @param tag      the tag of the disk
     * @param capacity the capacity of the disk
     * @return the storage
     */
    IStorageDisk<FluidStack> createFluidStorage(NBTTagCompound tag, int capacity);

    /**
     * Returns a NBT share tag for a disk.
     *
     * @param type  the type of disk
     * @param stack the disk
     * @return the share tag
     */
    NBTTagCompound getShareTag(StorageDiskType type, ItemStack stack);

    /**
     * Returns a initial base NBT tag for a disk.
     *
     * @param type the disk type
     * @return the tag
     */
    NBTTagCompound getTag(StorageDiskType type);

    /**
     * Initializes a disk with the base NBT tag.
     *
     * @param type  the disk type
     * @param stack the disk
     * @return the initialized disk
     */
    default ItemStack initDisk(StorageDiskType type, ItemStack stack) {
        stack.setTagCompound(getTag(type));

        return stack;
    }
}
