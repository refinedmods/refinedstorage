package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeTransferCraftingGridError implements IRecipeTransferError {
    protected static final Color AUTOCRAFTING_HIGHLIGHT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);
    private static final Color MISSING_HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    private static final boolean HOST_OS_IS_MACOS = System.getProperty("os.name").equals("Mac OS X");
    protected final IngredientTracker tracker;

    public RecipeTransferCraftingGridError(IngredientTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void showError(PoseStack stack, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        List<Component> message = drawIngredientHighlights(stack, recipeX, recipeY);

        Screen currentScreen = Minecraft.getInstance().screen;
        currentScreen.renderComponentTooltip(stack, message, mouseX, mouseY);
    }

    protected List<Component> drawIngredientHighlights(PoseStack stack, int recipeX, int recipeY) {
        List<Component> message = new ArrayList<>();
        message.add(new TranslatableComponent("jei.tooltip.transfer"));

        boolean craftMessage = false;
        boolean missingMessage = false;

        for (Ingredient ingredient : tracker.getIngredients()) {
            if (!ingredient.isAvailable()) {
                if (ingredient.isCraftable()) {
                    ingredient.getGuiIngredient().drawHighlight(stack, AUTOCRAFTING_HIGHLIGHT_COLOR.getRGB(), recipeX, recipeY);
                    craftMessage = true;
                } else {
                    ingredient.getGuiIngredient().drawHighlight(stack, MISSING_HIGHLIGHT_COLOR.getRGB(), recipeX, recipeY);
                    missingMessage = true;
                }
            }
        }

        if (missingMessage) {
            message.add(new TranslatableComponent("jei.tooltip.error.recipe.transfer.missing").withStyle(ChatFormatting.RED));
        }

        if (craftMessage) {
            if (HOST_OS_IS_MACOS) {
                message.add(new TranslatableComponent("gui.refinedstorage.jei.transfer.request_autocrafting_mac").withStyle(ChatFormatting.BLUE));
            } else {
                message.add(new TranslatableComponent("gui.refinedstorage.jei.transfer.request_autocrafting").withStyle(ChatFormatting.BLUE));
            }
        }

        return message;
    }
}
