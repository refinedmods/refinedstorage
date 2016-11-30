package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageFluidNBT;
import com.raoulvdberge.refinedstorage.block.EnumFluidStorageType;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ItemFluidStorageDisk extends ItemBase {
    public static final int TYPE_64K = 0;
    public static final int TYPE_128K = 1;
    public static final int TYPE_256K = 2;
    public static final int TYPE_512K = 3;
    public static final int TYPE_CREATIVE = 4;
    public static final int TYPE_DEBUG = 5;

    private NBTTagCompound debugDiskTag;

    public ItemFluidStorageDisk() {
        super("fluid_storage_disk");

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 5; ++i) {
            subItems.add(StorageFluidNBT.createStackWithNBT(new ItemStack(item, 1, i)));
        }
    }

    private void applyDebugDiskData(ItemStack stack) {
        if (debugDiskTag == null) {
            debugDiskTag = StorageFluidNBT.createNBT();

            StorageFluidNBT storage = new StorageFluidNBT(debugDiskTag, -1, null) {
                @Override
                public int getPriority() {
                    return 0;
                }

                @Override
                public boolean isVoiding() {
                    return false;
                }
            };

            for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
                storage.insert(new FluidStack(fluid, 0), Fluid.BUCKET_VOLUME * 1000, false);
            }

            storage.writeToNBT();
        }

        stack.setTagCompound(debugDiskTag.copy());
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            if (stack.getMetadata() == TYPE_DEBUG) {
                applyDebugDiskData(stack);
            } else {
                StorageFluidNBT.createStackWithNBT(stack);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack disk = player.getHeldItem(hand);

        if (!world.isRemote && player.isSneaking() && StorageFluidNBT.isValid(disk) && StorageFluidNBT.getStoredFromNBT(disk.getTagCompound()) <= 0 && disk.getMetadata() != TYPE_CREATIVE) {
            ItemStack storagePart = new ItemStack(RSItems.FLUID_STORAGE_PART, 1, disk.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING));
        }

        return new ActionResult<>(EnumActionResult.PASS, disk);
    }

    @Override
    public void addInformation(ItemStack disk, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (StorageFluidNBT.isValid(disk)) {
            int capacity = EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

            if (capacity == -1) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", StorageFluidNBT.getStoredFromNBT(disk.getTagCompound())));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", StorageFluidNBT.getStoredFromNBT(disk.getTagCompound()), capacity));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        StorageFluidNBT.createStackWithNBT(stack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return StorageFluidNBT.getNBTShareTag(stack.getTagCompound());
    }
}
