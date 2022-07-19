package com.refinedmods.refinedstorage.integration.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;

import java.util.UUID;

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
        return getMissingAmount() == 0;
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
}
