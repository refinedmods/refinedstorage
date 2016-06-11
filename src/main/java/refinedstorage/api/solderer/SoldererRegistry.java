package refinedstorage.api.solderer;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The recipe registry of the solderer.
 */
public class SoldererRegistry {
    private static List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

    /**
     * Adds a recipe to the registry.
     *
     * @param recipe
     */
    public static void addRecipe(@Nonnull ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    /**
     * @return An immutable recipe list
     */
    public static ImmutableList<ISoldererRecipe> getRecipes() {
        return ImmutableList.copyOf(recipes);
    }

    /**
     * @param items An item handler, where slots 0 - 2 are the row slots
     * @return The recipe
     */
    public static ISoldererRecipe getRecipe(@Nonnull IItemHandler items) {
        for (ISoldererRecipe recipe : recipes) {
            boolean found = true;

            for (int i = 0; i < 3; ++i) {
                if (!RefinedStorageUtils.compareStackNoQuantity(recipe.getRow(i), items.getStackInSlot(i)) && !RefinedStorageUtils.compareStackOreDict(recipe.getRow(i), items.getStackInSlot(i))) {
                    found = false;
                }

                if (items.getStackInSlot(i) != null && recipe.getRow(i) != null) {
                    if (items.getStackInSlot(i).stackSize < recipe.getRow(i).stackSize) {
                        found = false;
                    }
                }
            }

            if (found) {
                return recipe;
            }
        }

        return null;
    }
}
