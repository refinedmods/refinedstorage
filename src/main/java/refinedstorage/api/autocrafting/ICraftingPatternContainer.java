package refinedstorage.api.autocrafting;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

/**
 * Represents the container where the pattern is in.
 */
public interface ICraftingPatternContainer {
    /**
     * This usually corresponds to the amount of speed upgrades in a crafter.
     *
     * @return The speed of this container
     */
    int getSpeed();

    /**
     * @return The {@link IItemHandler} that this container is facing
     */
    IItemHandler getConnectedItems();

    /**
     * @return The patterns stored in this container
     */
    IItemHandler getPatterns();

    /**
     * @return The position of this container
     */
    BlockPos getPosition();
}
