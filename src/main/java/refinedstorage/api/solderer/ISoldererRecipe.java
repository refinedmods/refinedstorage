package refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a recipe in the solderer.
 */
public interface ISoldererRecipe {
    /**
     * @param row The solderer row (between 0 - 2)
     * @return A stack for the given row
     */
    @Nullable
    ItemStack getRow(int row);

    /**
     * @return The stack that this recipe gives back
     */
    @Nonnull
    ItemStack getResult();

    /**
     * @return The duration in ticks
     */
    int getDuration();
}
