package refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a recipe in the solderer.
 */
public interface ISoldererRecipe {
    /**
     * @param row The row in the solderer that we want the {@link ItemStack} for (between 0 - 2)
     * @return A {@link ItemStack} for the given row
     */
    @Nullable
    ItemStack getRow(int row);

    /**
     * @return The {@link ItemStack} that this recipe gives back
     */
    @Nonnull
    ItemStack getResult();

    /**
     * @return The duration in ticks that this recipe takes to give the result back from {@link ISoldererRecipe#getResult()}
     */
    int getDuration();
}
