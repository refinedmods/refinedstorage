package refinedstorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.api.RSAPI;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.api.solderer.ISoldererRegistry;

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
    public ISoldererRecipe getRecipe(@Nonnull IItemHandler rows) {
        for (ISoldererRecipe recipe : recipes) {
            boolean found = true;

            for (int i = 0; i < 3; ++i) {
                if (!RSAPI.instance().getComparer().isEqualNoQuantity(recipe.getRow(i), rows.getStackInSlot(i)) && !RSAPI.instance().getComparer().isEqualOredict(recipe.getRow(i), rows.getStackInSlot(i))) {
                    found = false;
                }

                ItemStack row = recipe.getRow(i);

                if (rows.getStackInSlot(i) != null && row != null) {
                    if (rows.getStackInSlot(i).stackSize < row.stackSize) {
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
