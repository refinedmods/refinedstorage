package refinedstorage.tile.autocrafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import refinedstorage.container.ContainerDummy;
import refinedstorage.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

public class CraftingTask {
    private ItemStack result;
    private List<ItemStack> ingredients;

    public CraftingTask(ItemStack result, List<ItemStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public static CraftingTask create(ItemStack result) {
        List<ItemStack> ingredients = new ArrayList<ItemStack>();

        addCraftingIngredients(ingredients, result);

        return new CraftingTask(result, ingredients);
    }

    private static void addCraftingIngredients(List<ItemStack> ingredients, ItemStack stack) {
        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            ItemStack output = recipe.getRecipeOutput();
            // this may seem unnecessary but keep it, some horrible mods return a null itemstack
            if (output != null && output.getItem() != null) {
                // first check if the output is the stack we're adding the ingredients for
                if (InventoryUtils.compareStack(output, stack)) {
                    // now get all the ingredients from that recipe
                    for (ItemStack ingredient : recipe.getRemainingItems(new InventoryCrafting(new ContainerDummy(), 3, 3))) {
                        ingredients.add(ingredient);
                    }
                }
            }
        }
    }
}
