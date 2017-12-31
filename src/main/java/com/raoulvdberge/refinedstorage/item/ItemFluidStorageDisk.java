package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.block.FluidStorageType;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidStorageDisk extends ItemBase implements IStorageDiskProvider<FluidStack> {
    public static final int TYPE_64K = 0;
    public static final int TYPE_128K = 1;
    public static final int TYPE_256K = 2;
    public static final int TYPE_512K = 3;
    public static final int TYPE_CREATIVE = 4;
    public static final int TYPE_DEBUG = 5;

    public ItemFluidStorageDisk() {
        super("fluid_storage_disk");

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i < 5; ++i) {
            items.add(API.instance().getDefaultStorageDiskBehavior().initDisk(StorageDiskType.FLUIDS, new ItemStack(this, 1, i)));
        }
    }

    private void applyDebugDiskData(ItemStack stack) {
        NBTTagCompound debugDiskTag = API.instance().getDefaultStorageDiskBehavior().getTag(StorageDiskType.FLUIDS);

        StorageDiskFluid storage = new StorageDiskFluid(debugDiskTag, -1);

        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            storage.insert(new FluidStack(fluid, 0), Fluid.BUCKET_VOLUME * 1000, false);
        }

        storage.writeToNBT();

        stack.setTagCompound(debugDiskTag);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            if (stack.getMetadata() == TYPE_DEBUG) {
                applyDebugDiskData(stack);
            } else {
                API.instance().getDefaultStorageDiskBehavior().initDisk(StorageDiskType.FLUIDS, stack);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack disk = player.getHeldItem(hand);

        IStorageDisk storage = create(disk);

        if (!world.isRemote && player.isSneaking() && storage.isValid(disk) && storage.getStored() <= 0 && disk.getMetadata() != TYPE_CREATIVE) {
            ItemStack storagePart = new ItemStack(RSItems.FLUID_STORAGE_PART, 1, disk.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING));
        }

        return new ActionResult<>(EnumActionResult.PASS, disk);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        IStorageDisk storage = create(stack);

        if (storage.isValid(stack)) {
            if (storage.getCapacity() == -1) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", RenderUtils.QUANTITY_FORMATTER_UNFORMATTED.format(storage.getStored())));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", RenderUtils.QUANTITY_FORMATTER_UNFORMATTED.format(storage.getStored()), RenderUtils.QUANTITY_FORMATTER_UNFORMATTED.format(storage.getCapacity())));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        API.instance().getDefaultStorageDiskBehavior().initDisk(StorageDiskType.FLUIDS, stack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return API.instance().getDefaultStorageDiskBehavior().getShareTag(StorageDiskType.FLUIDS, stack);
    }

    @Nonnull
    @Override
    public IStorageDisk<FluidStack> create(ItemStack disk) {
        return API.instance().getDefaultStorageDiskBehavior().createFluidStorage(disk.getTagCompound(), FluidStorageType.getById(disk.getItemDamage()).getCapacity());
    }
}
