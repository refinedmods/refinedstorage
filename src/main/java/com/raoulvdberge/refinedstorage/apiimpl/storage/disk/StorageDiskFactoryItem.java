package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class StorageDiskFactoryItem implements IStorageDiskFactory<ItemStack> {
    public static final String ID = "normal_item";

    @Override
    public IStorageDisk<ItemStack> createFromNbt(World world, NBTTagCompound tag) {
        StorageDiskItem disk = new StorageDiskItem(world, tag.getInteger(StorageDiskItem.NBT_CAPACITY));

        NBTTagList list = (NBTTagList) tag.getTag(StorageDiskItem.NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompoundTagAt(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        disk.recalculateStored();

        return disk;
    }

    @Override
    public IStorageDisk<ItemStack> create(World world, int capacity) {
        return new StorageDiskItem(world, capacity);
    }
}
