package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.util.IOneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.UUID;

public class OneSixMigrationHelper implements IOneSixMigrationHelper {
    // 1.5.x NBT keys
    private static final String NBT_ITEMS = "Items";
    private static final String NBT_ITEM_TYPE = "Type";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_DAMAGE = "Damage";
    private static final String NBT_ITEM_NBT = "NBT";
    private static final String NBT_ITEM_CAPS = "Caps";

    private static final String NBT_FLUIDS = "Fluids";

    @Override
    public boolean migrateDisk(World world, ItemStack disk) {
        IStorageDiskProvider provider = (IStorageDiskProvider) disk.getItem();

        switch (provider.getType()) {
            case ITEM:
                if (disk.hasTagCompound() && disk.getTagCompound().hasKey(NBT_ITEMS)) {
                    UUID id = UUID.randomUUID();

                    IStorageDisk<ItemStack> newDisk = API.instance().createDefaultItemDisk(world, provider.getCapacity(disk));

                    NBTTagList list = (NBTTagList) disk.getTagCompound().getTag(NBT_ITEMS);

                    for (int i = 0; i < list.tagCount(); ++i) {
                        NBTTagCompound tag = list.getCompoundTagAt(i);

                        ItemStack stack = new ItemStack(
                            Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
                            tag.getInteger(NBT_ITEM_QUANTITY),
                            tag.getInteger(NBT_ITEM_DAMAGE),
                            tag.hasKey(NBT_ITEM_CAPS) ? tag.getCompoundTag(NBT_ITEM_CAPS) : null
                        );

                        stack.setTagCompound(tag.hasKey(NBT_ITEM_NBT) ? tag.getCompoundTag(NBT_ITEM_NBT) : null);

                        if (!stack.isEmpty()) {
                            newDisk.insert(stack, stack.getCount(), false);
                        }
                    }

                    API.instance().getStorageDiskManager(world).set(id, newDisk);
                    API.instance().getStorageDiskManager(world).markForSaving();

                    provider.setId(disk, id);

                    return true;
                }

                break;
            case FLUID:
                if (disk.hasTagCompound() && disk.getTagCompound().hasKey(NBT_FLUIDS)) {
                    UUID id = UUID.randomUUID();

                    IStorageDisk<FluidStack> newDisk = API.instance().createDefaultFluidDisk(world, provider.getCapacity(disk));

                    NBTTagList list = (NBTTagList) disk.getTagCompound().getTag(NBT_FLUIDS);

                    for (int i = 0; i < list.tagCount(); ++i) {
                        FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i));

                        if (stack != null) {
                            newDisk.insert(stack, stack.amount, false);
                        }
                    }

                    API.instance().getStorageDiskManager(world).set(id, newDisk);
                    API.instance().getStorageDiskManager(world).markForSaving();

                    provider.setId(disk, id);

                    return true;
                }

                break;
        }

        return false;
    }

    @Override
    public boolean migrateDiskInventory(World world, IItemHandlerModifiable handler) {
        boolean migrated = false;

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack disk = handler.getStackInSlot(i);

            if (!disk.isEmpty() && disk.getItem() instanceof IStorageDiskProvider) {
                if (migrateDisk(world, disk)) {
                    handler.setStackInSlot(i, disk); // Trigger a onContentsChanged

                    migrated = true;
                }
            }
        }

        return migrated;
    }
}
