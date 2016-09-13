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
     * @param recipe the recipe to add
     */
    void addRecipe(@Nonnull ISoldererRecipe recipe);

    /**
     * Returns a solderer recipe from the rows.
     *
     * @param rows an item handler, where slots 0 - 2 are the rows
     * @return the {@link ISoldererRecipe}, or null if no recipe was found
     */
    @Nullable
    ISoldererRecipe getRecipe(@Nonnull IItemHandler rows);

    /**
     * @return a list with all the solderer recipes, do NOT modify
     */
    List<ISoldererRecipe> getRecipes();
}
