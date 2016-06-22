package refinedstorage.jei;

import net.minecraft.item.ItemStack;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.solderer.ISoldererRecipe;

import java.util.ArrayList;
import java.util.List;

public class SoldererRecipeMaker {
    public static List<SoldererRecipeWrapper> getRecipes() {
        List<SoldererRecipeWrapper> recipes = new ArrayList<SoldererRecipeWrapper>();

        for (ISoldererRecipe recipe : RefinedStorageAPI.SOLDERER_REGISTRY.getRecipes()) {
            List<ItemStack> inputs = new ArrayList<ItemStack>();

            inputs.add(recipe.getRow(0));
            inputs.add(recipe.getRow(1));
            inputs.add(recipe.getRow(2));

            ItemStack output = recipe.getResult();

            recipes.add(new SoldererRecipeWrapper(inputs, output));
        }

        return recipes;
    }
}
