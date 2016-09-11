package refinedstorage.api.autocrafting;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

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
     * @return the {@link IItemHandler} that this container is facing
     */
    IItemHandler getConnectedItems();

    /**
     * @return the patterns stored in this container
     */
    IItemHandler getPatterns();

    /**
     * @return the position of this container
     */
    BlockPos getPosition();
}
