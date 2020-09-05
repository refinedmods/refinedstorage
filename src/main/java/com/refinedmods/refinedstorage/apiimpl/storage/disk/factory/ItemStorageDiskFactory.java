package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.item.StorageDiskItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class ItemStorageDiskFactory implements IStorageDiskFactory<ItemStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");

    @Override
    public IStorageDisk<ItemStack> createFromNbt(ServerWorld world, CompoundNBT tag) {
        ItemStorageDisk disk = new ItemStorageDisk(world, tag.getInt(ItemStorageDisk.NBT_CAPACITY));

        ListNBT list = tag.getList(ItemStorageDisk.NBT_ITEMS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompound(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        return disk;
    }

    @Override
    public ItemStack createDiskItem(IStorageDisk<ItemStack> disk, UUID id) {
        StorageDiskItem item;
        switch (disk.getCapacity()) {
            case 1_000:
                item = RSItems.ONE_K_STORAGE_DISK;
                break;
            case 4_000:
                item = RSItems.FOUR_K_STORAGE_DISK;
                break;
            case 16_000:
                item = RSItems.SIXTEEN_K_STORAGE_DISK;
                break;
            case 64_000:
                item = RSItems.SIXTY_FOUR_K_STORAGE_DISK;
                break;
            default:
                item = RSItems.CREATIVE_STORAGE_DISK;
                break;
        }

        ItemStack stack = new ItemStack(item);
        item.setId(stack, id);
        return stack;
    }

    @Override
    public IStorageDisk<ItemStack> create(ServerWorld world, int capacity) {
        return new ItemStorageDisk(world, capacity);
    }
}
