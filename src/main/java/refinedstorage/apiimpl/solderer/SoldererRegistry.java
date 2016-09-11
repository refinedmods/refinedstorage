package refinedstorage.apiimpl.solderer;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.api.solderer.ISoldererRegistry;
import refinedstorage.api.storage.CompareUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SoldererRegistry implements ISoldererRegistry {
    private List<ISoldererRecipe> recipes = new ArrayList<>();

    @Override
    public void addRecipe(@Nonnull ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    @Nullable
    public ISoldererRecipe getRecipe(@Nonnull IItemHandler row) {
        for (ISoldererRecipe recipe : recipes) {
            boolean found = true;

            for (int i = 0; i < 3; ++i) {
                if (!CompareUtils.compareStackNoQuantity(recipe.getRow(i), row.getStackInSlot(i)) && !CompareUtils.compareStackOreDict(recipe.getRow(i), row.getStackInSlot(i))) {
                    found = false;
                }

                if (row.getStackInSlot(i) != null && recipe.getRow(i) != null) {
                    if (row.getStackInSlot(i).stackSize < recipe.getRow(i).stackSize) {
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

    @Override
    public List<ISoldererRecipe> getRecipes() {
        return recipes;
    }
}
