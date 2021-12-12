package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class RecipeTransferPatternGridError extends RecipeTransferCraftingGridError {
    public RecipeTransferPatternGridError(IngredientTracker tracker) {
        super(tracker);
    }

    @Override
    protected List<ITextComponent> drawIngredientHighlights(MatrixStack stack, int recipeX, int recipeY) {
        List<ITextComponent> message = new ArrayList<>();
        message.add(new TranslationTextComponent("jei.tooltip.transfer"));

        boolean craftMessage = false;

        for (Ingredient ingredient : tracker.getIngredients()) {
            if (ingredient.isCraftable()) {
                ingredient.getGuiIngredient().drawHighlight(stack, AUTOCRAFTING_HIGHLIGHT_COLOR.getRGB(), recipeX, recipeY);
                craftMessage = true;
            }
        }

        if (craftMessage) {
            message.add(new TranslationTextComponent("gui.refinedstorage.jei.transfer.autocrafting_available").withStyle(TextFormatting.BLUE));
        }

        return message;
    }
}
