package com.raoulvdberge.refinedstorage.item.itemblock;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.NetworkNodeFluidStorage;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.block.BlockFluidStorage;
import com.raoulvdberge.refinedstorage.item.ItemFluidStorageDisk;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemBlockFluidStorage extends ItemBlockBase {
    public ItemBlockFluidStorage(BlockFluidStorage block) {
        super(block, true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack)) {
            UUID id = getId(stack);

            API.instance().getStorageDiskSync().sendRequest(id);

            IStorageDiskSyncData data = API.instance().getStorageDiskSync().getData(id);
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(I18n.format("misc.refinedstorage:storage.stored", API.instance().getQuantityFormatter().format(data.getStored())));
                } else {
                    tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())));
                }
            }

            if (flag.isAdvanced()) {
                tooltip.add(id.toString());
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack storageStack = player.getHeldItem(hand);

        if (!world.isRemote && player.isSneaking() && storageStack.getMetadata() != ItemFluidStorageDisk.TYPE_CREATIVE) {
            UUID diskId = null;
            IStorageDisk disk = null;

            if (isValid(storageStack)) {
                diskId = getId(storageStack);
                disk = API.instance().getStorageDiskManager(world).get(diskId);
            }

            // Newly created storages won't have a tag yet, so allow invalid disks as well.
            if (disk == null || disk.getStored() == 0) {
                ItemStack storagePart = new ItemStack(RSItems.FLUID_STORAGE_PART, storageStack.getCount(), storageStack.getMetadata());

                if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
                }

                ItemStack processor = new ItemStack(RSItems.PROCESSOR, storageStack.getCount(), ItemProcessor.TYPE_BASIC);

                if (!player.inventory.addItemStackToInventory(processor.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), processor);
                }

                if (disk != null) {
                    API.instance().getStorageDiskManager(world).remove(diskId);
                    API.instance().getStorageDiskManager(world).markForSaving();
                }

                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSBlocks.MACHINE_CASING));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, storageStack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    private UUID getId(ItemStack disk) {
        return disk.getTagCompound().getUniqueId(NetworkNodeFluidStorage.NBT_ID);
    }

    private boolean isValid(ItemStack disk) {
        return disk.hasTagCompound() && disk.getTagCompound().hasUniqueId(NetworkNodeFluidStorage.NBT_ID);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);

        if (!world.isRemote) {
            OneSixMigrationHelper.migrateFluidStorageBlockItem(world, stack);
        }
    }
}
