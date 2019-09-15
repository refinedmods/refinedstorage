package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public class StorageDiskFactoryItem implements IStorageDiskFactory<ItemStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");

    @Override
    public IStorageDisk<ItemStack> createFromNbt(ServerWorld world, CompoundNBT tag) {
        StorageDiskItem disk = new StorageDiskItem(world, tag.getInt(StorageDiskItem.NBT_CAPACITY));

        ListNBT list = tag.getList(StorageDiskItem.NBT_ITEMS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompound(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<ItemStack> create(ServerWorld world, int capacity) {
        return new StorageDiskItem(world, capacity);
    }
}
