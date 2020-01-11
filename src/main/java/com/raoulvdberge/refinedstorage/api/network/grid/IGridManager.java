package com.raoulvdberge.refinedstorage.api.network.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Manages {@link IGridFactory} instances and has code that opens grids.
 */
public interface IGridManager {
    /**
     * @param factory the factory
     * @return the id of this factory
     */
    int add(IGridFactory factory);

    /**
     * @param id the id of the factory
     * @return the factory, or null if no factory was found
     */
    @Nullable
    IGridFactory get(int id);

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id as returned from {@link #add(IGridFactory)}
     * @param player the player
     * @param pos    the block position
     */
    void openGrid(int id, EntityPlayerMP player, BlockPos pos);

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id as returned from {@link #add(IGridFactory)}
     * @param player the player
     * @param stack  the stack
     * @param slotId the slot id, if applicable, otherwise -1
     */
    void openGrid(int id, EntityPlayerMP player, ItemStack stack, int slotId);

    /**
     * Creates a grid.
     *
     * @param id     the grid factory id as returned from {@link #add(IGridFactory)}
     * @param player the player
     * @param stack  the stack, if there is one
     * @param pos    the block position, if there is one
     * @param slotId the slot id, if applicable, otherwise -1
     * @return a grid, or null if an error has occurred
     */
    @Nullable
    Pair<IGrid, TileEntity> createGrid(int id, EntityPlayer player, @Nullable ItemStack stack, @Nullable BlockPos pos, int slotId);
}
