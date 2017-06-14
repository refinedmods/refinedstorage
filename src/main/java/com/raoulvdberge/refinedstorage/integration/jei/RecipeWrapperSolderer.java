package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RecipeWrapperSolderer implements IRecipeWrapper {
    private IDrawableAnimated progress;

    private List<List<ItemStack>> inputs;
    private ItemStack output;

    public RecipeWrapperSolderer(IGuiHelper guiHelper, int duration, List<List<ItemStack>> inputs, ItemStack output) {
        this.progress = guiHelper.createAnimatedDrawable(
            guiHelper.createDrawable(
                new ResourceLocation("refinedstorage", "textures/gui/solderer.png"),
                212,
                0,
                22,
                15
            ),
            duration,
            IDrawableAnimated.StartDirection.LEFT,
            false
        );

        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        progress.draw(minecraft, 40, 18);
    }
}
