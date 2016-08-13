package refinedstorage.api.network.grid;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Defines the behavior of item grids.
 */
public interface IFluidGridHandler {
    /**
     * Called when a player tries to extract a fluid from the grid.
     *
     * @param hash   The hash of the fluid we're trying to extract, see {@link refinedstorage.api.network.NetworkUtils#getFluidStackHashCode(net.minecraftforge.fluids.FluidStack)}
     * @param shift  If we're shift clicking
     * @param player The player that is attempting the extraction
     */
    void onExtract(int hash, boolean shift, EntityPlayerMP player);

    /**
     * Called when a player tries to insert fluids to the grid.
     *
     * @param container A stack with a container we're trying to insert
     * @return The remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onInsert(ItemStack container);

    /**
     * Called when a player is trying to insert a fluid that it is holding in their hand in the GUI.
     *
     * @param player The player that is attempting the insert
     */
    void onInsertHeldContainer(EntityPlayerMP player);
}
