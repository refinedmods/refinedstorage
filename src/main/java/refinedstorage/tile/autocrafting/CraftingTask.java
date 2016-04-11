package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import refinedstorage.tile.TileController;
import refinedstorage.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

public class CraftingTask {
    private ItemStack result;
    private List<CraftingIngredient> ingredients;

    public CraftingTask(ItemStack result, List<CraftingIngredient> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean attemptCraft(TileController controller) {
        for (CraftingIngredient ingredient : ingredients) {
            if (!ingredient.isSatisfied()) {
                ItemStack took = controller.take(ingredient.getStack().copy());

                if (took != null) {
                    ingredient.setSatisfied();
                } else if (!ingredient.isSubtaskCreated()) {
                    CraftingTask subTask = CraftingTask.create(ingredient.getStack());
                    ingredient.setSubtaskCreated();
                    controller.addCraftingTask(subTask);
                }
            }
        }

        for (CraftingIngredient ingredient : ingredients) {
            if (!ingredient.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    public static CraftingTask create(ItemStack result) {
        List<CraftingIngredient> ingredients = new ArrayList<CraftingIngredient>();

        addCraftingIngredients(ingredients, result);

        return new CraftingTask(result, ingredients);
    }

    private static void addCraftingIngredients(List<CraftingIngredient> ingredients, ItemStack result) {
        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            if (recipe instanceof ShapedRecipes) {
                ItemStack output = recipe.getRecipeOutput();
                // this may seem unnecessary but keep it, some mods return a null itemstack
                if (output != null && output.getItem() != null) {
                    // first check if the output is the stack we're adding the ingredients for
                    if (InventoryUtils.compareStack(output, result)) {
                        // now get all the ingredients from that recipe
                        for (ItemStack ingredient : ((ShapedRecipes) recipe).recipeItems) {
                            if (ingredient != null) {
                                ingredients.add(new CraftingIngredient(ingredient));
                            }
                        }
                    }
                }
            }
        }
    }
}
