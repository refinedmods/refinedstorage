package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerProcessingPatternEncoder;
import com.raoulvdberge.refinedstorage.network.MessageProcessingPatternEncoderTransfer;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class RecipeTransferHandlerPattern implements IRecipeTransferHandler<ContainerProcessingPatternEncoder> {
    @Override
    public Class<ContainerProcessingPatternEncoder> getContainerClass() {
        return ContainerProcessingPatternEncoder.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return "patternEncoding";
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerProcessingPatternEncoder container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            List<ItemStack> inputs = new LinkedList<>();
            List<ItemStack> outputs = new LinkedList<>();

            for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                    ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
                    if (guiIngredient.isInput()) {
                        inputs.add(ingredient);
                    } else {
                        outputs.add(ingredient);
                    }
                }
            }

            RS.INSTANCE.network.sendToServer(new MessageProcessingPatternEncoderTransfer(inputs, outputs));
        }

        return null;
    }
}
