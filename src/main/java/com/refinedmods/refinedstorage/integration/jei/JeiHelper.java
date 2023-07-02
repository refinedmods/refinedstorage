package com.refinedmods.refinedstorage.integration.jei;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;

import java.util.Optional;

public class JeiHelper {
    private final IIngredientManager ingredientManager;

    public JeiHelper(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    public Optional<IClickableIngredient<?>> makeClickableIngredient(Object ingredient, Rect2i area) {
        return ingredientManager.createTypedIngredient(ingredient).map(ti -> new ClickableIngredient<>(ti, area));
    }

    private record ClickableIngredient<T>(ITypedIngredient<T> ingredient,
                                          Rect2i area) implements IClickableIngredient<T> {
        @Override
        public ITypedIngredient<T> getTypedIngredient() {
            return ingredient;
        }

        @Override
        public Rect2i getArea() {
            return area;
        }
    }
}
