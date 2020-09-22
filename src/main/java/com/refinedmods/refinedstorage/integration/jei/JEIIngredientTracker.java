package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JEIIngredientTracker {

    List<AvailableIngredient> ingredients = new ArrayList<>();

    public void init(IRecipeLayout recipeLayout) {
        for (IGuiIngredient<ItemStack> ingredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                ingredients.add(new AvailableIngredient(ingredient));
            }
        }
    }

    public List<AvailableIngredient> getIngredients() {
        return ingredients;
    }

    public void checkIfStackIsNeeded(ItemStack stack, boolean isCraftable) {
        int available = stack.getCount();

        for (AvailableIngredient ingredient : ingredients) {
            if (available == 0) {
                return;
            }

            if (ingredient.isAvailable()) {
                continue;
            }

            Optional<?> match = ingredient.guiIngredient.getAllIngredients().stream().filter((ItemStack matchingStack) -> API.instance().getComparer().isEqual(matchingStack, matchingStack, IComparer.COMPARE_NBT)).findFirst();
            if (match.isPresent()) {
                //craftables and non craftables are 2 different gridstacks
                if (isCraftable) {
                    ingredient.isCraftable = true;
                } else {
                    int needed = ingredient.required - ingredient.fulfilled;
                    int used = Math.min(available, needed);
                    ingredient.fulfilled += used;
                    available -= used;
                }
            }
        }
    }

    public boolean hasMissing() {
        return ingredients.stream().anyMatch(x -> !x.isAvailable());
    }

    static class AvailableIngredient {
        IGuiIngredient<ItemStack> guiIngredient;
        boolean isCraftable;
        int required;
        int fulfilled;

        public AvailableIngredient(IGuiIngredient<ItemStack> guiIngredient) {
            this.guiIngredient = guiIngredient;
            required = guiIngredient.getAllIngredients().get(0).getCount();
        }

        public boolean isAvailable() {
            return required == fulfilled;
        }

        public boolean isCraftable() {
            return isCraftable;
        }
    }
}
