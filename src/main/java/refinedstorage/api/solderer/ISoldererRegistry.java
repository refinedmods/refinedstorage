package refinedstorage.api.solderer;

import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * The recipe registry of the solderer.
 */
public interface ISoldererRegistry {
    /**
     * Adds a recipe to the registry.
     *
     * @param recipe The recipe to add
     */
    void addRecipe(@Nonnull ISoldererRecipe recipe);

    /**
     * @return A list with all the solderer recipes, do NOT modify
     */
    List<ISoldererRecipe> getRecipes();

    /**
     * @param items An item handler, where slots 0 - 2 are the rows
     * @return The recipe, or null if no recipe was found
     */
    @Nullable
    ISoldererRecipe getRecipe(@Nonnull IItemHandler items);
}
