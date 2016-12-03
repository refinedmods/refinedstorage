package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageItemNBT;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockStorage extends ItemBlockBase {
    public ItemBlockStorage() {
        super(RSBlocks.STORAGE, RSBlocks.STORAGE.getPlacementType(), true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        EnumItemStorageType type = EnumItemStorageType.getById(stack.getMetadata());

        if (type != null && isValid(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE);

            if (type == EnumItemStorageType.TYPE_CREATIVE) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", StorageItemNBT.getStoredFromNBT(tag)));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", StorageItemNBT.getStoredFromNBT(tag), type.getCapacity()));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        EnumItemStorageType type = EnumItemStorageType.getById(stack.getMetadata());

        if (type != null && stack.getCount() == 1 && isValid(stack) && StorageItemNBT.getStoredFromNBT(stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE)) <= 0 && stack.getMetadata() != ItemStorageDisk.TYPE_CREATIVE && !world.isRemote && player.isSneaking()) {
            ItemStack storagePart = new ItemStack(RSItems.STORAGE_PART, 1, stack.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            ItemStack processor = new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC);

            if (!player.inventory.addItemStackToInventory(processor.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), processor);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSBlocks.MACHINE_CASING));
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private static boolean isValid(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileStorage.NBT_STORAGE);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        if (!isValid(stack)) {
            return super.getNBTShareTag(stack);
        } else {
            NBTTagCompound shareTag = new NBTTagCompound();
            shareTag.setTag(TileStorage.NBT_STORAGE, StorageItemNBT.getNBTShareTag(stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE)));
            return shareTag;
        }
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(TileStorage.NBT_STORAGE, StorageItemNBT.createNBT());
        stack.setTagCompound(tag);
        return stack;
    }
}
