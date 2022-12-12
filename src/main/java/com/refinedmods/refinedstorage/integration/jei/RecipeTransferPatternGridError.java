package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RecipeTransferPatternGridError extends RecipeTransferCraftingGridError {
    public RecipeTransferPatternGridError(Ingredient.IngredientList ingredients) {
        super(ingredients);
    }

    @Override
    protected List<Component> drawIngredientHighlights(PoseStack stack, int recipeX, int recipeY) {
        List<Component> message = new ArrayList<>();
        message.add(Component.translatable("jei.tooltip.transfer"));

        boolean craftMessage = false;

        for (Ingredient ingredient : ingredientList.ingredients) {
            if (ingredient.isCraftable()) {
                ingredient.getSlotView().drawHighlight(stack, AUTOCRAFTING_HIGHLIGHT_COLOR.getRGB());
                craftMessage = true;
            }
        }

        if (craftMessage) {
            message.add(Component.translatable("gui.refinedstorage.jei.transfer.autocrafting_available").withStyle(ChatFormatting.BLUE));
        }

        return message;
    }
}
