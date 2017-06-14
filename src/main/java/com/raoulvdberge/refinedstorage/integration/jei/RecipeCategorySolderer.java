package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RecipeCategorySolderer implements IRecipeCategory<RecipeWrapperSolderer> {
    public static final String ID = "refinedstorage.solderer";

    private IDrawable background;

    public RecipeCategorySolderer(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation("refinedstorage", "textures/gui/solderer.png"), 43, 19, 101, 54);
    }

    @Override
    public String getUid() {
        return ID;
    }

    @Override
    public String getTitle() {
        return I18n.format("gui.refinedstorage:solderer");
    }

    @Override
    public String getModName() {
        return I18n.format("itemGroup.refinedstorage");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull RecipeWrapperSolderer recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();

        int x = 0;
        int y = 0;

        for (int i = 0; i < 3; ++i) {
            group.init(i, true, x, y);

            y += 18;
        }

        group.init(3, false, 83, 18);

        for (int i = 0; i < 3; ++i) {
            group.set(i, ingredients.getInputs(ItemStack.class).get(i));
        }

        group.set(3, ingredients.getOutputs(ItemStack.class).get(0));
    }
}
