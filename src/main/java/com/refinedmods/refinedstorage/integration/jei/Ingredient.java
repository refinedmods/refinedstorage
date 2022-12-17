package com.refinedmods.refinedstorage.integration.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;

import java.util.*;

class Ingredient {
    private final IRecipeSlotView slotView;
    private final int required;
    private UUID craftStackId;
    private int fulfilled;

    public Ingredient(IRecipeSlotView view, int count) {
        this.slotView = view;
        this.required = count;
    }

    public boolean isAvailable() {
        return getMissingAmount() <= 0;
    }

    public int getMissingAmount() {
        return required - fulfilled;
    }

    public boolean isCraftable() {
        return craftStackId != null;
    }

    public IRecipeSlotView getSlotView() {
        return slotView;
    }

    public UUID getCraftStackId() {
        return craftStackId;
    }

    public void setCraftStackId(UUID craftStackId) {
        this.craftStackId = craftStackId;
    }

    public void fulfill(int amount) {
        fulfilled += amount;
    }

    static class IngredientList {
        List<Ingredient> ingredients = new ArrayList<>();

        void add(Ingredient ingredient) {
            ingredients.add(ingredient);
        }

        public boolean hasMissing() {
            return ingredients.stream().anyMatch(ingredient -> !ingredient.isAvailable());
        }

        public boolean hasMissingButAutocraftingAvailable() {
            return ingredients.stream().anyMatch(ingredient -> !ingredient.isAvailable() && ingredient.isCraftable());
        }

        public boolean isAutocraftingAvailable() {
            return ingredients.stream().anyMatch(Ingredient::isCraftable);
        }

        public Map<UUID, Integer> createCraftingRequests() {
            Map<UUID, Integer> toRequest = new HashMap<>();

            for (Ingredient ingredient : ingredients) {
                if (!ingredient.isAvailable() && ingredient.isCraftable()) {
                    toRequest.merge(ingredient.getCraftStackId(), ingredient.getMissingAmount(), Integer::sum);
                }
            }

            return toRequest;
        }
    }
}
