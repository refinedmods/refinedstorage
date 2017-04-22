package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RecipeWrapperSolderer extends BlankRecipeWrapper {
    private IDrawableAnimated progress;

    private List<ItemStack> inputs;
    private ItemStack output;

    public RecipeWrapperSolderer(IGuiHelper guiHelper, int duration, List<ItemStack> inputs, ItemStack output) {
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
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeWrapperSolderer)) {
            return false;
        }

        RecipeWrapperSolderer other = (RecipeWrapperSolderer) obj;

        for (int i = 0; i < inputs.size(); i++) {
            if (!ItemStack.areItemStacksEqual(inputs.get(i), other.inputs.get(i))) {
                return false;
            }
        }

        return ItemStack.areItemStacksEqual(output, other.output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

        progress.draw(minecraft, 40, 18);
    }

    @Override
    public String toString() {
        return inputs + " = " + output;
    }
}
