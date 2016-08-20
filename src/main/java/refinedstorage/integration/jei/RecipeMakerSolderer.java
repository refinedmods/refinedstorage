package refinedstorage.integration.jei;

import net.minecraft.item.ItemStack;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.solderer.ISoldererRecipe;

import java.util.ArrayList;
import java.util.List;

public final class RecipeMakerSolderer {
    public static List<RecipeWrapperSolderer> getRecipes() {
        List<RecipeWrapperSolderer> recipes = new ArrayList<>();

        for (ISoldererRecipe recipe : RefinedStorageAPI.SOLDERER_REGISTRY.getRecipes()) {
            List<ItemStack> inputs = new ArrayList<>();

            inputs.add(recipe.getRow(0));
            inputs.add(recipe.getRow(1));
            inputs.add(recipe.getRow(2));

            ItemStack output = recipe.getResult();

            recipes.add(new RecipeWrapperSolderer(inputs, output));
        }

        return recipes;
    }
}
