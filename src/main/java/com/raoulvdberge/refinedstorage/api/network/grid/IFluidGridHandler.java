package com.raoulvdberge.refinedstorage.api.network.grid;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Defines the behavior of fluid grids.
 */
public interface IFluidGridHandler {
    /**
     * Called when a player tries to extract a fluid from the grid.
     *
     * @param hash   the hash of the fluid we're trying to extract, see {@link IRSAPI#getFluidStackHashCode(FluidStack)}
     * @param shift  true if shift click was used, false otherwise
     * @param player the player that is attempting the extraction
     */
    void onExtract(int hash, boolean shift, EntityPlayerMP player);

    /**
     * Called when a player tries to insert fluids in the grid.
     *
     * @param container a stack with a fluid container we're trying to insert
     * @return the remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onInsert(ItemStack container);

    /**
     * Called when a player is trying to insert a fluid that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     */
    void onInsertHeldContainer(EntityPlayerMP player);
}
