package refinedstorage.tile.autocrafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import refinedstorage.container.ContainerDummy;
import refinedstorage.tile.TileController;
import refinedstorage.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

public class CraftingTask {
    private ItemStack result;
    private List<CraftingIngredient> ingredients;
    private CraftingTask parentTask;

    public CraftingTask(ItemStack result, List<CraftingIngredient> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public CraftingTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(CraftingTask parentTask) {
        this.parentTask = parentTask;
    }

    public boolean attemptCraft(TileController controller) {
        for (CraftingIngredient ingredient : ingredients) {
            if (!ingredient.isSatisfied()) {
                ItemStack took = controller.take(ingredient.getStack().copy());

                if (took != null) {
                    ingredient.setSatisfied();
                } else {
                    // schedule a crafting task, if it doesn't exist yet
                    for (CraftingTask task : controller.getCraftingTasks()) {
                        if (InventoryUtils.compareStack(task.getResult(), result) & task.getParentTask() == this) {
                            return false;
                        }
                    }

                    CraftingTask subTask = CraftingTask.create(ingredient.getStack());
                    subTask.setParentTask(this);
                    controller.getCraftingTasks().add(subTask);
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

    private static void addCraftingIngredients(List<CraftingIngredient> ingredients, ItemStack stack) {
        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            ItemStack output = recipe.getRecipeOutput();
            // this may seem unnecessary but keep it, some horrible mods return a null itemstack
            if (output != null && output.getItem() != null) {
                boolean hasIngredients = false;

                // first check if the output is the stack we're adding the ingredients for
                if (InventoryUtils.compareStack(output, stack)) {
                    // now get all the ingredients from that recipe
                    for (ItemStack ingredient : recipe.getRemainingItems(new InventoryCrafting(new ContainerDummy(), 3, 3))) {
                        ingredients.add(new CraftingIngredient(ingredient));
                        hasIngredients = true;
                        break;
                    }
                }

                if (!hasIngredients) {
                    ingredients.add(new CraftingIngredient(stack));
                }
            }
        }
    }
}
