package com.raoulvdberge.refinedstorage.api.autocrafting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents a network node that contains crafting patterns.
 */
public interface ICraftingPatternContainer {
    /**
     * @return the amount of speed upgrades in the container
     */
    int getSpeedUpdateCount();

    /**
     * @return the inventory that this container is connected to
     */
    IItemHandler getConnectedInventory();

    /**
     * @return the tile that this container is connected to
     */
    TileEntity getConnectedTile();

    /**
     * @return the tile that this container is facing
     */
    TileEntity getFacingTile();

    /**
     * @return the patterns stored in this container
     */
    List<ICraftingPattern> getPatterns();

    /**
     * @return the pattern inventory, or null if no pattern inventory is present
     */
    @Nullable
    IItemHandlerModifiable getPatternInventory();

    /**
     * The name of this container for categorizing in the Crafting Manager GUI.
     * Can be a localized or unlocalized name.
     * If it's unlocalized, it will automatically localize the name.
     *
     * @return the name of this container
     */
    String getName();

    /**
     * @return the position of this container
     */
    BlockPos getPosition();

    /**
     * Containers may be daisy-chained together.  If this container points to
     * another one, gets the last container in the chain.  Otherwise, this
     * container is the last in the chain.
     *
     * @return the root pattern container
     */
    @Nullable
    ICraftingPatternContainer getProxyPatternContainer();

    /**
     * @return true if this container or its proxy is blocked, false otherwise
     */
    boolean isBlocked();

    /**
     * @param blocked whether the container should be blocked
     */
    void setBlocked(boolean blocked);

    /**
     * @param blockedOn the descendant that this container is blocked on
     */
    void setBlockedOn(UUID blockedOn);
}
