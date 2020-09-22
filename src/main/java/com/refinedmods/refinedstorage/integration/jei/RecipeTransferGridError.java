package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.util.TemporaryPortingUtils;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeTransferGridError implements IRecipeTransferError {
    private static final Color HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    private static final Color HIGHLIGHT_AUTOCRAFT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);
    private final List<IFormattableTextComponent> message = new ArrayList<>();
    boolean craftMessage;
    boolean missingMessage;
    JEIIngredientTracker tracker;

    public RecipeTransferGridError(JEIIngredientTracker tracker) {
        this.message.add(new TranslationTextComponent("jei.tooltip.transfer"));
        this.tracker = tracker;
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void showError(MatrixStack stack, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        tracker.getIngredients().forEach(x -> drawIngredientHighlight(stack, x, recipeX, recipeY));
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        TemporaryPortingUtils.drawHoveringText(ItemStack.EMPTY, stack, message, mouseX, mouseY, currentScreen.width, currentScreen.height, 150, Minecraft.getInstance().fontRenderer);
    }

    private void drawIngredientHighlight(MatrixStack stack, JEIIngredientTracker.AvailableIngredient<?> ingredient, int recipeX, int recipeY) {
        if (!ingredient.isAvailable()) {
            if (ingredient.isCraftable()) {
                ingredient.guiIngredient.drawHighlight(stack, HIGHLIGHT_AUTOCRAFT_COLOR.getRGB(), recipeX, recipeY);
                if (!craftMessage) {
                    message.add(new TranslationTextComponent(TextFormatting.BLUE + I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.missing.autocraft")));
                    message.add(new StringTextComponent(TextFormatting.BLUE + "Ctrl Click to request craftable items"));
                    craftMessage = true;
                }
            } else {
                ingredient.guiIngredient.drawHighlight(stack, HIGHLIGHT_COLOR.getRGB(), recipeX, recipeY);
                if (!missingMessage) {
                    message.add(new TranslationTextComponent(TextFormatting.RED + I18n.format("jei.tooltip.error.recipe.transfer.missing")));
                    missingMessage = true;
                }
            }
        }
    }
}
