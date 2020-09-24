package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.item.ItemStack;

import java.util.*;

public class JEIIngredientTracker {
    private final List<AvailableIngredient> ingredients = new ArrayList<>();

    public JEIIngredientTracker(IRecipeLayout recipeLayout) {
        for (IGuiIngredient<ItemStack> ingredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                ingredients.add(new AvailableIngredient(ingredient));
            }
        }
    }

    public Collection<AvailableIngredient> getIngredients() {
        return ingredients;
    }

    public void checkIfStackIsNeeded(ItemStack stack, IGridStack gridStack) {
        int available = stack.getCount();

        for (AvailableIngredient ingredient : ingredients) {
            if (available == 0) {
                return;
            }

            if (ingredient.isAvailable()) {
                continue;
            }

            Optional<?> match = ingredient.guiIngredient.getAllIngredients().stream().filter((ItemStack matchingStack) -> API.instance().getComparer().isEqual(stack, matchingStack, IComparer.COMPARE_NBT)).findFirst();
            if (match.isPresent()) {
                //craftables and non-craftables are 2 different gridstacks
                //as such we need to ignore craftable stacks as they are not actual items
                if (gridStack != null && gridStack.isCraftable()) {
                    ingredient.craftID = gridStack.getId();
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

    public Map<UUID, Integer> getCraftingRequests() {
        Map<UUID, Integer> toRequest = new HashMap<>();

        for (AvailableIngredient ingredient : ingredients) {
            if (!ingredient.isAvailable() && ingredient.isCraftable()) {
                toRequest.merge(ingredient.craftID, ingredient.getMissingAmount(), Integer::sum);
            }
        }

        return toRequest;
    }

    static class AvailableIngredient {
        IGuiIngredient<ItemStack> guiIngredient;
        UUID craftID;
        int required;
        int fulfilled;

        public AvailableIngredient(IGuiIngredient<ItemStack> guiIngredient) {
            this.guiIngredient = guiIngredient;
            required = guiIngredient.getAllIngredients().get(0).getCount();
        }

        public boolean isAvailable() {
            return getMissingAmount() == 0;
        }

        public int getMissingAmount() {
            return required - fulfilled;
        }

        public boolean isCraftable() {
            return craftID != null;
        }

        public void addRequired(int required) {
            this.required += required;
        }
    }
}
