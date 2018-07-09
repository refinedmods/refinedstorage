package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.block.enums.ItemStorageType;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemStorageDisk extends ItemBase implements IStorageDiskProvider {
    private static final String NBT_ID = "Id";

    public static final int TYPE_1K = 0;
    public static final int TYPE_4K = 1;
    public static final int TYPE_16K = 2;
    public static final int TYPE_64K = 3;
    public static final int TYPE_CREATIVE = 4;

    public ItemStorageDisk() {
        super(new ItemInfo(RS.ID, "storage_disk"));

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelVariants(
            this,
            new ResourceLocation(RS.ID, "1k_storage_disk"),
            new ResourceLocation(RS.ID, "4k_storage_disk"),
            new ResourceLocation(RS.ID, "16k_storage_disk"),
            new ResourceLocation(RS.ID, "64k_storage_disk"),
            new ResourceLocation(RS.ID, "creative_storage_disk")
        );

        modelRegistration.setModel(this, TYPE_1K, new ModelResourceLocation(RS.ID + ":1k_storage_disk", "inventory"));
        modelRegistration.setModel(this, TYPE_4K, new ModelResourceLocation(RS.ID + ":4k_storage_disk", "inventory"));
        modelRegistration.setModel(this, TYPE_16K, new ModelResourceLocation(RS.ID + ":16k_storage_disk", "inventory"));
        modelRegistration.setModel(this, TYPE_64K, new ModelResourceLocation(RS.ID + ":64k_storage_disk", "inventory"));
        modelRegistration.setModel(this, TYPE_CREATIVE, new ModelResourceLocation(RS.ID + ":creative_storage_disk", "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i < 5; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!world.isRemote) {
            if (!isValid(stack)) {
                API.instance().getOneSixMigrationHelper().migrateDisk(world, stack);
            }

            if (!stack.hasTagCompound()) {
                UUID id = UUID.randomUUID();

                API.instance().getStorageDiskManager(world).set(id, API.instance().createDefaultItemDisk(world, getCapacity(stack)));
                API.instance().getStorageDiskManager(world).markForSaving();

                setId(stack, id);
            }
        }
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
        ItemStack diskStack = player.getHeldItem(hand);

        if (!world.isRemote && player.isSneaking() && diskStack.getMetadata() != TYPE_CREATIVE) {
            IStorageDisk disk = API.instance().getStorageDiskManager(world).getByStack(diskStack);

            if (disk != null && disk.getStored() == 0) {
                ItemStack storagePart = new ItemStack(RSItems.STORAGE_PART, diskStack.getCount(), diskStack.getMetadata());

                if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
                }

                API.instance().getStorageDiskManager(world).remove(getId(diskStack));
                API.instance().getStorageDiskManager(world).markForSaving();

                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, diskStack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public UUID getId(ItemStack disk) {
        return disk.getTagCompound().getUniqueId(NBT_ID);
    }

    @Override
    public void setId(ItemStack disk, UUID id) {
        disk.setTagCompound(new NBTTagCompound());
        disk.getTagCompound().setUniqueId(NBT_ID, id);
    }

    @Override
    public boolean isValid(ItemStack disk) {
        return disk.hasTagCompound() && disk.getTagCompound().hasUniqueId(NBT_ID);
    }

    @Override
    public int getCapacity(ItemStack disk) {
        return ItemStorageType.getById(disk.getItemDamage()).getCapacity();
    }

    @Override
    public StorageType getType() {
        return StorageType.ITEM;
    }
}
