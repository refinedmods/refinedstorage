package refinedstorage.api.autocrafting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * Represents the container where a crafting pattern is in.
 */
public interface ICraftingPatternContainer {
    /**
     * The speed that crafting tasks that have a pattern in this container can run.
     *
     * @return the speed of this container
     */
    int getSpeed();

    /**
     * @return the tile that this container is facing
     */
    TileEntity getFacingTile();

    /**
     * @return the inventory that this container is facing
     */
    IItemHandler getFacingInventory();

    /**
     * @return the patterns stored in this container
     */
    List<ICraftingPattern> getPatterns();

    /**
     * @return the position of this container
     */
    BlockPos getPosition();
}
