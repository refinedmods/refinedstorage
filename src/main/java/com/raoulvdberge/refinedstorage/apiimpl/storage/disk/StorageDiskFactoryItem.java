package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import net.minecraft.item.Item;
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
            NBTTagCompound item = list.getCompoundTagAt(i);

            ItemStack stack = new ItemStack(
                Item.getItemById(item.getInteger(StorageDiskItem.NBT_ITEM_TYPE)),
                item.getInteger(StorageDiskItem.NBT_ITEM_QUANTITY),
                item.getInteger(StorageDiskItem.NBT_ITEM_DAMAGE),
                item.hasKey(StorageDiskItem.NBT_ITEM_CAPS) ? item.getCompoundTag(StorageDiskItem.NBT_ITEM_CAPS) : null
            );

            stack.setTagCompound(item.hasKey(StorageDiskItem.NBT_ITEM_NBT) ? item.getCompoundTag(StorageDiskItem.NBT_ITEM_NBT) : null);

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<ItemStack> create(World world, int capacity) {
        return new StorageDiskItem(world, capacity);
    }
}
