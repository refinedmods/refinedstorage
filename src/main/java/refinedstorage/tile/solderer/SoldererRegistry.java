package refinedstorage.tile.solderer;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;

import java.util.ArrayList;
import java.util.List;

public class SoldererRegistry {
    public static List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

    public static void addRecipe(ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    public static ISoldererRecipe getRecipe(IItemHandler items) {
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
