package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidStorage;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.block.FluidStorageType;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockFluidStorage extends ItemBlockBase {
    public ItemBlockFluidStorage() {
        super(RSBlocks.FLUID_STORAGE, RSBlocks.FLUID_STORAGE.getDirection(), true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        FluidStorageType type = FluidStorageType.getById(stack.getMetadata());

        if (type != null && isValid(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(NetworkNodeFluidStorage.NBT_STORAGE);

            if (type == FluidStorageType.TYPE_CREATIVE) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", StorageDiskFluid.getStored(tag)));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", StorageDiskFluid.getStored(tag), type.getCapacity()));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        FluidStorageType type = FluidStorageType.getById(stack.getMetadata());

        if (type != null && stack.getCount() == 1 && isValid(stack) && StorageDiskFluid.getStored(stack.getTagCompound().getCompoundTag(NetworkNodeFluidStorage.NBT_STORAGE)) <= 0 && stack.getMetadata() != ItemFluidStorageDisk.TYPE_CREATIVE && !world.isRemote && player.isSneaking()) {
            ItemStack storagePart = new ItemStack(RSItems.FLUID_STORAGE_PART, 1, stack.getMetadata());

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
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(NetworkNodeFluidStorage.NBT_STORAGE);
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
            shareTag.setTag(NetworkNodeFluidStorage.NBT_STORAGE, StorageDiskFluid.getShareTag(stack.getTagCompound().getCompoundTag(NetworkNodeFluidStorage.NBT_STORAGE)));
            return shareTag;
        }
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(NetworkNodeFluidStorage.NBT_STORAGE, StorageDiskFluid.getTag());
        stack.setTagCompound(tag);
        return stack;
    }
}
