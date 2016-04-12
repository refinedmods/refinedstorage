package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileController;

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
                    System.out.println("Ingredient " + ingredient.getStack() + " SATISFIED");
                    ingredient.setSatisfied();
                } else if (!ingredient.isSubtaskCreated()) {
                    System.out.println("Ingredient " + ingredient.getStack() + " NOT SATISFIED, creating subtask");

                    ItemStack pattern = controller.getPatternForItem(ingredient.getStack());

                    if (pattern != null) {
                        System.out.println("Found a pattern for this!");
                        CraftingTask subTask = CraftingTask.createFromPattern(pattern);
                        ingredient.setSubtaskCreated();
                        controller.addCraftingTask(subTask);
                        break;
                    }
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

    public static CraftingTask createFromPattern(ItemStack pattern) {
        System.out.println("Creating crafting task for " + ItemPattern.getResult(pattern));
        List<CraftingIngredient> ingredients = new ArrayList<CraftingIngredient>();

        for (int i = 0; i < 9; ++i) {
            ItemStack ingredient = ItemPattern.getSlot(pattern, i);

            if (ingredient != null) {
                System.out.println("Ingredient #" + i + ": " + ingredient);
                ingredients.add(new CraftingIngredient(ingredient));
            }
        }

        return new CraftingTask(ItemPattern.getResult(pattern), ingredients);
    }
}
