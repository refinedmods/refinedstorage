package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.UUID;

/**
 * A helper for the changes in 1.6.
 */
public interface IOneSixMigrationHelper {
    /**
     * Migrates this disk over to the new 1.6 format *IF POSSIBLE*.
     * The given disk item needs to implement {@link com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider}!
     * This will call {@link com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider#setId(ItemStack, UUID)} on the disk, so make sure
     * a call to this method clears previous 1.5 and lower data, or this method will keep migrating constantly!
     * <p>
     * This will *ONLY* work with disks that were made using the IStorageDiskBehavior 1.5 API.
     * If you were using another method, you'll have to implement the migration code yourself.
     *
     * @param world the world
     * @param disk  the disk to attempt to migrate
     * @return true if it migrated, false otherwise
     */
    boolean migrateDisk(World world, ItemStack disk);

    /**
     * Migrates an entire inventory.
     * Loops over every slot in the inventory and calls {@link #migrateDisk(World, ItemStack)}.
     * All the docs from {@link #migrateDisk(World, ItemStack)} apply here as well!
     * Don't forget to mark your tile dirty if this call returns true!
     *
     * @param world   the world
     * @param handler the inventory
     * @return true if it migrated something in the inventory, false otherwise
     */
    boolean migrateDiskInventory(World world, IItemHandlerModifiable handler);
}
