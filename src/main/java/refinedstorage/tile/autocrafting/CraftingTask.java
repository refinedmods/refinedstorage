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

    public void attemptCraft(TileController controller) {
        for (CraftingIngredient ingredient : ingredients) {
            if (!ingredient.isSatisfied()) {
                ItemStack took = controller.take(ingredient.getStack().copy());

                if (took != null) {
                    ingredient.setSatisfied();
                } else if (!ingredient.isChildTaskCreated()) {
                    ItemStack pattern = controller.getPatternForItem(ingredient.getStack());

                    if (pattern != null) {
                        CraftingTask childTask = CraftingTask.createFromPattern(pattern);
                        ingredient.setChildTaskCreated();
                        controller.addCraftingTask(childTask);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }

    public boolean isDone() {
        for (CraftingIngredient ingredient : ingredients) {
            if (!ingredient.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    public static CraftingTask createFromPattern(ItemStack pattern) {
        List<CraftingIngredient> ingredients = new ArrayList<CraftingIngredient>();

        for (int i = 0; i < 9; ++i) {
            ItemStack ingredient = ItemPattern.getSlot(pattern, i);

            if (ingredient != null) {
                ingredients.add(new CraftingIngredient(ingredient));
            }
        }

        return new CraftingTask(ItemPattern.getResult(pattern), ingredients);
    }
}
