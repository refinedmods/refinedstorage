package com.raoulvdberge.refinedstorage.api.autocrafting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

/**
 * Represents a network node that contains crafting patterns.
 */
public interface ICraftingPatternContainer {
    /**
     * @return the amount of speed upgrades in the container
     */
    int getSpeedUpdateCount();

    /**
     * @return the inventory that this container is facing
     */
    IItemHandler getFacingInventory();

    /**
     * @return the tile that this container is facing
     */
    TileEntity getFacingTile();

    /**
     * @return the patterns stored in this container
     */
    List<ICraftingPattern> getPatterns();

    /**
     * @return the pattern inventory
     */
    IItemHandlerModifiable getPatternInventory();

    /**
     * @return the name of this container
     */
    String getName();

    /**
     * @return the position of this container
     */
    BlockPos getPosition();

    /**
     * @return true if this container is blocked, false otherwise
     */
    boolean isBlocked();

    /**
     * @param blocked whether the container should be blocked
     */
    void setBlocked(boolean blocked);
}
