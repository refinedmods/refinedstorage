package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RecipeTransferErrorAutocraftSlots implements IRecipeTransferError {
    private static final Color HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    private static final Color HIGHLIGHT_AUTOCRAFT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);
    private final Collection<Integer> slots;
    private final Collection<Integer> autocraftSlots;
    private final List<String> message = new ArrayList<>();

    public RecipeTransferErrorAutocraftSlots(String message, String autocraftMessage, Collection<Integer> slots, Collection<Integer> autocraftSlots) {
        this.message.add(I18n.format("jei.tooltip.transfer"));
        this.message.add(TextFormatting.RED + message);
        this.message.add(TextFormatting.BLUE + autocraftMessage);

        this.slots = slots;
        this.autocraftSlots = autocraftSlots;
    }

    @Override
    public Type getType() {
        return Type.USER_FACING;
    }

    @Override
    public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = itemStackGroup.getGuiIngredients();
        for (Integer slotIndex : slots) {
            IGuiIngredient<ItemStack> ingredient = ingredients.get(slotIndex);
            ingredient.drawHighlight(minecraft, HIGHLIGHT_COLOR, recipeX, recipeY);
        }
        for (Integer slotIndex : autocraftSlots) {
            IGuiIngredient<ItemStack> ingredient = ingredients.get(slotIndex);
            ingredient.drawHighlight(minecraft, HIGHLIGHT_AUTOCRAFT_COLOR, recipeX, recipeY);
        }
        ScaledResolution scaledresolution = new ScaledResolution(minecraft);
        GuiUtils.drawHoveringText(ItemStack.EMPTY, message, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 150, minecraft.fontRenderer);
    }
}
