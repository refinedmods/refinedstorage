package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
                if (!API.instance().getComparer().isEqualNoQuantity(recipe.getRow(i), rows.getStackInSlot(i)) && !API.instance().getComparer().isEqualOredict(recipe.getRow(i), rows.getStackInSlot(i))) {
                    found = false;
                }

                ItemStack row = recipe.getRow(i);

                if (rows.getStackInSlot(i) != null && row != null) {
                    if (rows.getStackInSlot(i).getCount() < row.getCount()) {
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

    @Nonnull
    @Override
    public ISoldererRecipe createSimpleRecipe(@Nonnull ItemStack result, int duration, ItemStack... rows) {
        if (rows.length != 3) {
            throw new IllegalArgumentException("Solderer recipe expects 3 rows, got " + rows.length + " rows");
        }

        return new ISoldererRecipe() {
            @Nullable
            @Override
            public ItemStack getRow(int row) {
                return rows[row];
            }

            @Nonnull
            @Override
            public ItemStack getResult() {
                return result;
            }

            @Override
            public int getDuration() {
                return duration;
            }
        };
    }

    @Override
    public List<ISoldererRecipe> removeRecipe(@Nonnull ItemStack result, ItemStack... rows) {
        if (!(rows.length == 0 || rows.length == 3)) {
            throw new IllegalArgumentException("Removing a recipe requires either no rows or 3 rows, got " + rows.length + " rows");
        }
        Iterator<ISoldererRecipe> itr = recipes.iterator();
        List<ISoldererRecipe> removed = new LinkedList<>();
        while (itr.hasNext()) {
            ISoldererRecipe recipe = itr.next();
            if (API.instance().getComparer().isEqualNoQuantity(result, recipe.getResult())) {
                if (rows.length == 0 || compareRows(recipe, rows)) {
                    itr.remove();
                    removed.add(recipe);
                }
            }
        }
        return removed;
    }

    private boolean compareRows(ISoldererRecipe recipe, ItemStack[] rows) {
        for (int i = 0; i < 3; ++i) {
            if(!API.instance().getComparer().isEqualNoQuantity(recipe.getRow(i), rows[i])) {
                return false;
            }
        }
        return true;
    }
}
