package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeTransferGridError implements IRecipeTransferError {
    private static final Color HIGHLIGHT_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.4f);
    private static final Color HIGHLIGHT_AUTOCRAFT_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.4f);
    private final List<String> message = new ArrayList<>();
    GridType gridType;
    boolean craftMessage;
    boolean missingMessage;
    List<GridRecipeTransferHandler.GuiIngredientAvailability> ingredients;
    List<GridRecipeTransferHandler.GuiIngredientAvailability> ingredientsFluids;

    public RecipeTransferGridError(GridType gridType, List<GridRecipeTransferHandler.GuiIngredientAvailability> ingredients, List<GridRecipeTransferHandler.GuiIngredientAvailability> ingredientsFluids) {
        this.gridType = gridType;
        this.ingredients = ingredients;
        this.ingredientsFluids = ingredientsFluids;
        this.message.add(I18n.format("jei.tooltip.transfer"));
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void showError(int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
        ingredients.forEach(x->drawIngredientHighlight(x,recipeX,recipeY));
        ingredientsFluids.forEach(x->drawIngredientHighlight(x,recipeX,recipeY));
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        GuiUtils.drawHoveringText(ItemStack.EMPTY, message, mouseX, mouseY, currentScreen.width, currentScreen.height, 150, Minecraft.getInstance().fontRenderer);
    }
    private void drawIngredientHighlight(GridRecipeTransferHandler.GuiIngredientAvailability ingredient, int recipeX, int recipeY){
        if(!ingredient.isAvailable()){
            if(ingredient.isCraftable()){
                ingredient.guiIngredient.drawHighlight(HIGHLIGHT_AUTOCRAFT_COLOR.getRGB(),recipeX,recipeY);
                if(!craftMessage){
                    message.add(TextFormatting.BLUE + I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.missing.autocraft"));
                    craftMessage = true;
                }
            } else {
                ingredient.guiIngredient.drawHighlight(HIGHLIGHT_COLOR.getRGB(),recipeX,recipeY);
                if(!missingMessage){
                    message.add(TextFormatting.RED + I18n.format("jei.tooltip.error.recipe.transfer.missing"));
                    missingMessage = true;
                }
            }
        }
    }
}
