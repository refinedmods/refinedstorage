package com.refinedmods.refinedstorage.api.autocrafting

import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.Fluid
import net.minecraft.inventory.Inventory
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import reborncore.api.blockentity.InventoryProvider
import reborncore.common.util.Tank
import java.util.*


/**
 * Represents a network node that contains crafting patterns.
 */
interface ICraftingPatternContainer {
    /**
     * Returns the interval of when a crafting step with a pattern in this container can update.
     * Minimum value is 0 (each tick).
     *
     *
     * Note: rather than maxing out the update interval, implementors might want to balance around [.getMaximumSuccessfulCraftingUpdates].
     * This method merely speeds up the update rate, it might be more interesting to increase the output rate in [.getMaximumSuccessfulCraftingUpdates].
     *
     * @return the update interval
     */
    fun getUpdateInterval(): Int {
        return 10
    }

    /**
     * Returns the amount of successful crafting updates that this container can have per crafting step update.
     * If this limit is reached, crafting patterns from this container won't be able to update until the next
     * eligible crafting step update interval from [.getUpdateInterval].
     *
     * @return the maximum amount of successful crafting updates
     */
    fun getMaximumSuccessfulCraftingUpdates(): Int {
        return 1
    }

    /**
     * @return the inventory that this container is connected to, or null if no inventory is present
     */
    fun getConnectedInventory(): Inventory?

    /**
     * @return the fluid inventory that this container is connected to, or null if no fluid inventory is present
     */
    fun getConnectedFluidInventory(): Tank?

    /**
     * @return the tile that this container is connected to, or null if no tile is present
     */
    fun getConnectedTile(): BlockEntity?

    /**
     * @return the tile that this container is facing
     */
    fun getFacingTile(): BlockEntity?

    /**
     * @return the direction to the facing tile
     */
    fun getDirection(): Direction?

    /**
     * @return the patterns stored in this container
     */
    fun getPatterns(): List<ICraftingPattern>

    /**
     * @return the pattern inventory, or null if no pattern inventory is present
     */
    fun getPatternInventory(): Inventory?

    /**
     * The name of this container for categorizing in the Crafting Manager GUI.
     *
     * @return the name of this container
     */
    fun getName(): Text

    /**
     * @return the position of this container
     */
    fun getPosition(): BlockPos?

    /**
     * Containers may be daisy-chained together.  If this container points to
     * another one, gets the root container in the chain.  If containers are
     * not daisy-chained, returns this container.  If there was a container
     * loop, returns null.
     *
     * @return the root pattern container
     */
    fun getRootContainer(): ICraftingPatternContainer?

    /**
     * @return the UUID of this container
     */
    fun getUuid(): UUID?

    /**
     * @return true if the connected inventory is locked for processing patterns, false otherwise
     */
    fun isLocked(): Boolean {
        return false
    }

    /**
     * Unlock the container so it may be used by processing pattern
     */
    fun unlock()

    /**
     * Called when this container is used by a processing pattern to insert items or fluids in the connected inventory.
     */
    fun onUsedForProcessing() {}
}