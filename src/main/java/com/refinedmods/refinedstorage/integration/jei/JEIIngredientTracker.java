package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JEIIngredientTracker {

    List<AvailableIngredient<?>> ingredients = new ArrayList<>();
    List<AvailableIngredient<?>> ingredientsFluids = new ArrayList<>();

    public void init(IRecipeLayout recipeLayout) {
        for (IGuiIngredient<ItemStack> ingredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                ingredients.add(new AvailableIngredient<>(ingredient));
            }
        }

        for (IGuiIngredient<FluidStack> ingredient : recipeLayout.getFluidStacks().getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                ingredientsFluids.add(new AvailableIngredient<>(ingredient));
            }
        }
    }

    public Stream<AvailableIngredient<?>> getIngredients() {
        return Stream.concat(ingredients.stream(), ingredientsFluids.stream());
    }

    public void checkIfStackIsNeeded(Object stack, boolean isCraftable) {
        boolean isItem = stack instanceof ItemStack;

        List<AvailableIngredient<?>> availableIngredients;
        int available;
        if (isItem) {
            available = ((ItemStack) stack).getCount();
            availableIngredients = ingredients;
        } else {
            available = ((FluidStack) stack).getAmount();
            availableIngredients = ingredientsFluids;
        }

        for (AvailableIngredient<?> ingredient : availableIngredients) {
            if (available == 0) {
                return;
            }

            if (ingredient.isAvailable()) {
                continue;
            }

            Optional<?> match = ingredient.guiIngredient.getAllIngredients().stream().filter(x -> {
                if (isItem) {
                    return API.instance().getComparer().isEqual((ItemStack) stack, (ItemStack) x, IComparer.COMPARE_NBT);
                } else {
                    return API.instance().getComparer().isEqual((FluidStack) stack, (FluidStack) x, IComparer.COMPARE_NBT);
                }
            }).findFirst();

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
        return ingredients.stream().anyMatch(x -> !x.isAvailable()) || ingredientsFluids.stream().anyMatch(x -> !x.isAvailable());
    }

    static class AvailableIngredient<T> {
        IGuiIngredient<T> guiIngredient;
        boolean isCraftable;
        int required;
        int fulfilled;

        public AvailableIngredient(IGuiIngredient<T> guiIngredient) {
            this.guiIngredient = guiIngredient;
            Object stack;
            stack = guiIngredient.getAllIngredients().get(0);
            if (stack instanceof ItemStack) {
                required = ((ItemStack) stack).getCount();
            } else if (stack instanceof FluidStack) {
                required = ((FluidStack) stack).getAmount();
            }
        }

        public boolean isAvailable() {
            return required == fulfilled;
        }

        public boolean isCraftable() {
            return isCraftable;
        }
    }
}
