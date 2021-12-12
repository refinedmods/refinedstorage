package com.refinedmods.refinedstorage.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.recipe.transfer.IRecipeTransferError.Type;

public class RecipeTransferCraftingGridError implements IRecipeTransferError {
    private static final Color MISSING_HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    protected static final Color AUTOCRAFTING_HIGHLIGHT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);

    protected final IngredientTracker tracker;

    public RecipeTransferCraftingGridError(IngredientTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void showError(MatrixStack stack, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        List<ITextComponent> message = drawIngredientHighlights(stack, recipeX, recipeY);

        Screen currentScreen = Minecraft.getInstance().screen;
        GuiUtils.drawHoveringText(ItemStack.EMPTY, stack, message, mouseX, mouseY, currentScreen.width, currentScreen.height, 200, Minecraft.getInstance().font);
    }

    protected List<ITextComponent> drawIngredientHighlights(MatrixStack stack, int recipeX, int recipeY) {
        List<ITextComponent> message = new ArrayList<>();
        message.add(new TranslationTextComponent("jei.tooltip.transfer"));

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
            message.add(new TranslationTextComponent("jei.tooltip.error.recipe.transfer.missing").withStyle(TextFormatting.RED));
        }

        if (craftMessage) {
            message.add(new TranslationTextComponent("gui.refinedstorage.jei.transfer.request_autocrafting").withStyle(TextFormatting.BLUE));
        }

        return message;
    }
}
