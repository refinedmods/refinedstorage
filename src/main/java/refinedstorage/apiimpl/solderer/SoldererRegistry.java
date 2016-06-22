package refinedstorage.apiimpl.solderer;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.api.solderer.ISoldererRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SoldererRegistry implements ISoldererRegistry {
    private List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

    @Override
    public void addRecipe(@Nonnull ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public List<ISoldererRecipe> getRecipes() {
        return recipes;
    }

    @Override
    @Nullable
    public ISoldererRecipe getRecipe(@Nonnull IItemHandler items) {
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
