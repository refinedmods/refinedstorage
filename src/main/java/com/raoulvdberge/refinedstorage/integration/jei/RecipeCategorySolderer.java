package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeCategorySolderer implements IRecipeCategory {
    public static final String ID = "refinedstorage.solderer";

    private IDrawable background;

    public RecipeCategorySolderer(IGuiHelper helper) {
        background = helper.createDrawable(new ResourceLocation("refinedstorage", "textures/gui/solderer.png"), 43, 19, 101, 54);
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
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();

        int x = 0;
        int y = 0;

        for (int i = 0; i < 3; ++i) {
            group.init(i, true, x, y);

            y += 18;
        }

        group.init(3, false, 83, 18);

        if (recipeWrapper instanceof RecipeWrapperSolderer) {
            for (int i = 0; i < 3; ++i) {
                group.set(i, (ItemStack) recipeWrapper.getInputs().get(i));
            }

            group.set(3, (ItemStack) recipeWrapper.getOutputs().get(0));
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();

        int x = 0;
        int y = 0;

        for (int i = 0; i < 3; ++i) {
            group.init(i, true, x, y);

            y += 18;
        }

        group.init(3, false, 83, 18);

        if (recipeWrapper instanceof RecipeWrapperSolderer) {
            for (int i = 0; i < 3; ++i) {
                group.set(i, ingredients.getInputs(ItemStack.class).get(i));
            }

            group.set(3, ingredients.getOutputs(ItemStack.class).get(0));
        }
    }
}
