package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.item.StorageDiskItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemStorageDiskFactory implements IStorageDiskFactory<ItemStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");

    @Override
    public IStorageDisk<ItemStack> createFromNbt(ServerWorld world, CompoundNBT tag) {
        ItemStorageDisk disk = new ItemStorageDisk(
            world,
            tag.getInt(ItemStorageDisk.NBT_CAPACITY),
            tag.contains(ItemStorageDisk.NBT_OWNER) ? tag.getUUID(ItemStorageDisk.NBT_OWNER) : null
        );

        ListNBT list = tag.getList(ItemStorageDisk.NBT_ITEMS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompound(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        disk.updateItemCount();

        return disk;
    }

    @Override
    public ItemStack createDiskItem(IStorageDisk<ItemStack> disk, UUID id) {
        StorageDiskItem item;
        switch (disk.getCapacity()) {
            case 1_000:
                item = RSItems.ITEM_STORAGE_DISKS.get(ItemStorageType.ONE_K).get();
                break;
            case 4_000:
                item = RSItems.ITEM_STORAGE_DISKS.get(ItemStorageType.FOUR_K).get();
                break;
            case 16_000:
                item = RSItems.ITEM_STORAGE_DISKS.get(ItemStorageType.SIXTEEN_K).get();
                break;
            case 64_000:
                item = RSItems.ITEM_STORAGE_DISKS.get(ItemStorageType.SIXTY_FOUR_K).get();
                break;
            default:
                item = RSItems.ITEM_STORAGE_DISKS.get(ItemStorageType.CREATIVE).get();
                break;
        }

        ItemStack stack = new ItemStack(item);
        item.setId(stack, id);
        return stack;
    }

    @Override
    public IStorageDisk<ItemStack> create(ServerWorld world, int capacity, @Nullable UUID owner) {
        return new ItemStorageDisk(world, capacity, owner);
    }
}
