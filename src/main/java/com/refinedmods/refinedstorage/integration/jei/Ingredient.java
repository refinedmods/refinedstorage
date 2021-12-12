package com.refinedmods.refinedstorage.integration.jei;

import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

class Ingredient {
    private final IGuiIngredient<ItemStack> guiIngredient;
    private final int required;
    private UUID craftStackId;
    private int fulfilled;

    public Ingredient(IGuiIngredient<ItemStack> guiIngredient) {
        this.guiIngredient = guiIngredient;
        this.required = guiIngredient.getAllIngredients().get(0).getCount();
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

    public IGuiIngredient<ItemStack> getGuiIngredient() {
        return guiIngredient;
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
