package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.item.ItemStack;

import java.util.*;

public class IngredientTracker {
    private final List<Ingredient> ingredients = new ArrayList<>();

    public IngredientTracker(IRecipeLayout recipeLayout) {
        for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
            if (guiIngredient.isInput() && !guiIngredient.getAllIngredients().isEmpty()) {
                ingredients.add(new Ingredient(guiIngredient));
            }
        }
    }

    public Collection<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addAvailableStack(ItemStack stack, IGridStack gridStack) {
        int available = stack.getCount();

        for (Ingredient ingredient : ingredients) {
            if (available == 0) {
                return;
            }

            if (ingredient.isAvailable()) {
                continue;
            }

            Optional<?> match = ingredient.getGuiIngredient().getAllIngredients().stream().filter((ItemStack matchingStack) -> API.instance().getComparer().isEqual(stack, matchingStack, IComparer.COMPARE_NBT)).findFirst();
            if (match.isPresent()) {
                //craftables and non-craftables are 2 different gridstacks
                //as such we need to ignore craftable stacks as they are not actual items
                if (gridStack != null && gridStack.isCraftable()) {
                    ingredient.setCraftStackId(gridStack.getId());
                } else {
                    int needed = ingredient.getMissingAmount();
                    int used = Math.min(available, needed);
                    ingredient.fulfill(used);
                    available -= used;
                }
            }
        }
    }

    public boolean hasMissing() {
        return ingredients.stream().anyMatch(ingredient -> !ingredient.isAvailable());
    }

    public Map<UUID, Integer> getCraftingRequests() {
        Map<UUID, Integer> toRequest = new HashMap<>();

        for (Ingredient ingredient : ingredients) {
            if (!ingredient.isAvailable() && ingredient.isCraftable()) {
                toRequest.merge(ingredient.getCraftStackId(), ingredient.getMissingAmount(), Integer::sum);
            }
        }

        return toRequest;
    }
}
