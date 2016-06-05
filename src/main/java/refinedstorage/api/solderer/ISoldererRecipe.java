package refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;

/**
 * Represents a recipe in the solderer.
 */
public interface ISoldererRecipe {
    /**
     * @param row The solderer row (between 0 - 2)
     * @return A stack for the given row, can be null for an empty row
     */
    ItemStack getRow(int row);

    /**
     * @return The stack that this recipe gives back
     */
    ItemStack getResult();

    /**
     * @return The duration in ticks
     */
    int getDuration();
}
