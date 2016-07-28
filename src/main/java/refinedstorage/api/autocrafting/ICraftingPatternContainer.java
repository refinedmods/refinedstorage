package refinedstorage.api.autocrafting;

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
}
