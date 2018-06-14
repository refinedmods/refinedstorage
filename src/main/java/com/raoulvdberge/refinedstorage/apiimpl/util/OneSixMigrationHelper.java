package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.api.util.IOneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
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

    private static final String NBT_OUTPUTS = "Outputs";
    private static final String NBT_SLOT = "Slot_%d";

    public static boolean migratePattern(ItemStack pattern) {
        NBTTagCompound tag = pattern.getTagCompound();

        if (pattern.hasTagCompound() && !tag.hasKey(ItemPattern.NBT_PROCESSING)) {
            boolean isProcessing = tag.hasKey(NBT_OUTPUTS);

            // Add "processing" tag
            tag.setBoolean(ItemPattern.NBT_PROCESSING, isProcessing);

            // Convert "slot_%d" to "input_%d"
            for (int i = 0; i < 9; ++i) {
                String id = String.format(NBT_SLOT, i);

                if (tag.hasKey(id)) {
                    ItemStack slot = new ItemStack(pattern.getTagCompound().getCompoundTag(id));

                    if (!slot.isEmpty()) {
                        tag.setTag(String.format(ItemPattern.NBT_INPUT_SLOT, i), slot.serializeNBT());
                    }

                    tag.removeTag(id);
                }
            }

            // Convert "outputs" to "output_%d"
            if (isProcessing) {
                NBTTagList list = tag.getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < list.tagCount(); ++i) {
                    ItemStack output = new ItemStack(list.getCompoundTagAt(i));

                    if (!output.isEmpty()) {
                        tag.setTag(String.format(ItemPattern.NBT_OUTPUT_SLOT, i), output.serializeNBT());
                    }
                }

                tag.removeTag(NBT_OUTPUTS);
            }

            return true;
        }

        return false;
    }

    public static boolean migratePatternInventory(IItemHandler handler) {
        boolean migrated = false;

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack pattern = handler.getStackInSlot(i);

            if (!pattern.isEmpty() && migratePattern(pattern)) {
                migrated = true;
            }
        }

        return migrated;
    }

    // We check on NBT_PROCESSING, so it needs to be there for sure to be a valid pattern.
    public static boolean isValidOneSixPattern(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(ItemPattern.NBT_PROCESSING);
    }

    // If we remove the OneSixMigrationHelper we know where to remove other migration hooks by removing this method.
    public static void removalHook() {
    }

    public static void migrateEmptyWhitelistToEmptyBlacklist(String version, IFilterable filterable, @Nullable IItemHandler itemFilterInv, @Nullable IItemHandler fluidFilterInv) {
        // Only migrate if we come from a version where the RS version tag stuff in NetworkNode wasn't added yet.
        // Otherwise, we would constantly migrate empty whitelists to empty blacklists...
        if (version == null && filterable.getMode() == IFilterable.WHITELIST && IFilterable.isEmpty(itemFilterInv) && IFilterable.isEmpty(fluidFilterInv)) {
            filterable.setMode(IFilter.MODE_BLACKLIST);
        }
    }
}
