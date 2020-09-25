package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.util.TemporaryPortingUtils;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeTransferGridError implements IRecipeTransferError {
    private static final Color HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    private static final Color HIGHLIGHT_AUTOCRAFT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);
    IngredientTracker tracker;

    public RecipeTransferGridError(IngredientTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void showError(MatrixStack stack, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        List<ITextComponent> message = drawIngredientHighlights(stack, tracker, recipeX, recipeY);
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        TemporaryPortingUtils.drawHoveringText(ItemStack.EMPTY, stack, message, mouseX, mouseY, currentScreen.width, currentScreen.height, 150, Minecraft.getInstance().fontRenderer);
    }

    private List<ITextComponent> drawIngredientHighlights(MatrixStack stack, IngredientTracker tracker, int recipeX, int recipeY) {
        List<ITextComponent> message = new ArrayList<>();
        message.add(new TranslationTextComponent("jei.tooltip.transfer"));
        boolean craftMessage = false;
        boolean missingMessage = false;

        for (Ingredient ingredient : tracker.getIngredients()) {
            if (!ingredient.isAvailable()) {
                if (ingredient.isCraftable()) {
                    ingredient.getGuiIngredient().drawHighlight(stack, HIGHLIGHT_AUTOCRAFT_COLOR.getRGB(), recipeX, recipeY);
                    craftMessage = true;
                } else {
                    ingredient.getGuiIngredient().drawHighlight(stack, HIGHLIGHT_COLOR.getRGB(), recipeX, recipeY);
                    missingMessage = true;
                }
            }
        }

        if (missingMessage) {
            message.add(new TranslationTextComponent("jei.tooltip.error.recipe.transfer.missing").mergeStyle(TextFormatting.RED));
        }

        if (craftMessage) {
            message.add(new TranslationTextComponent("gui.refinedstorage.jei.tooltip.error.recipe.transfer.missing.autocraft").mergeStyle(TextFormatting.BLUE));
        }

        return message;
    }
}
