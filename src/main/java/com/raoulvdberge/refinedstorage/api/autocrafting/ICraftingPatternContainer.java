package com.raoulvdberge.refinedstorage.api.autocrafting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
    int getSpeedUpgradeCount();

    /**
     * @return the inventory that this container is connected to, or null if no inventory is present
     */
    @Nullable
    IItemHandler getConnectedInventory();

    /**
     * @return the fluid inventory that this container is connected to, or null if no fluid inventory is present
     */
    @Nullable
    IFluidHandler getConnectedFluidInventory();

    /**
     * @return the tile that this container is connected to, or null if no tile is present
     */
    @Nullable
    TileEntity getConnectedTile();

    /**
     * @return the tile that this container is facing
     */
    TileEntity getFacingTile();

    /**
     * @return the direction to the facing tile
     */
    EnumFacing getDirection();

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
     * another one, gets the root container in the chain.  If containers are
     * not daisy-chained, returns this container.  If there was a container
     * loop, returns null.
     *
     * @return the root pattern container
     */
    @Nullable
    ICraftingPatternContainer getRootContainer();

    /**
     * @return the UUID of this container
     */
    UUID getUuid();
}
