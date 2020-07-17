package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingPatternInputs {
    private final NonNullList<ItemStack> recipe = NonNullList.create();
    private final List<Ingredient> ingredients = new ArrayList<>();

    public CraftingPatternInputs(ICraftingPattern pattern) {
        fillOutRecipe(pattern);
        combineRecipeIntoIngredients(pattern);
    }

    private void fillOutRecipe(ICraftingPattern pattern) {
        for (NonNullList<ItemStack> inputsForSlot : pattern.getInputs()) {
            if (inputsForSlot.isEmpty()) {
                recipe.add(ItemStack.EMPTY);
            } else {
                recipe.add(inputsForSlot.get(0));
            }
        }
    }

    private void combineRecipeIntoIngredients(ICraftingPattern pattern) {
        for (NonNullList<ItemStack> inputsForSlot : pattern.getInputs()) {
            Ingredient matchingIngredient = findMatchingIngredient(inputsForSlot);

            if (matchingIngredient == null) {
                ingredients.add(new Ingredient(inputsForSlot, inputsForSlot.get(0).getCount()));
            } else {
                matchingIngredient.increaseCount(inputsForSlot.get(0).getCount());
            }
        }
    }

    @Nullable
    private Ingredient findMatchingIngredient(NonNullList<ItemStack> inputsForSlot) {
        for (Ingredient existingIngredient : ingredients) {
            if (existingIngredient.getInputs().size() == inputsForSlot.size()) {
                for (int i = 0; i < inputsForSlot.size(); i++) {
                    if (!API.instance().getComparer().isEqualNoQuantity(existingIngredient.getInputs().get(i), inputsForSlot.get(i))) {
                        break;
                    }
                }

                return existingIngredient;
            }
        }

        return null;
    }

    public NonNullList<ItemStack> getRecipe() {
        return recipe;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Ingredient {
        private final NonNullList<ItemStack> inputs;
        private int count;

        public Ingredient(NonNullList<ItemStack> inputs, int count) {
            this.inputs = inputs;
            this.count = count;
        }

        public NonNullList<ItemStack> getInputs() {
            return inputs;
        }

        public int getCount() {
            return count;
        }

        public void increaseCount(int count) {
            this.count += count;
        }
    }
}
