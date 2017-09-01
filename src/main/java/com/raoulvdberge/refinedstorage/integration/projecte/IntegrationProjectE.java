package com.raoulvdberge.refinedstorage.integration.projecte;

import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public final class IntegrationProjectE {
    private static final String ID = "projecte";

    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }

    public static void register() {
        for (ISoldererRecipe recipe : API.instance().getSoldererRegistry().getRecipes()) {
            if (!recipe.isProjectERecipe()) {
                continue;
            }

            Map<Object, Integer> ingredients = new HashMap<>();

            for (int i = 0; i < 3; ++i) {
                NonNullList<ItemStack> items = recipe.getRow(i);

                if (!items.isEmpty()) {
                    ingredients.put(items.get(0), items.get(0).getCount());
                }
            }

            ProjectEAPI.getConversionProxy().addConversion(recipe.getResult().getCount(), recipe.getResult(), ingredients);
        }
    }
}
