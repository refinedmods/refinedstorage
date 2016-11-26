package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerProcessingPatternEncoder;
import com.raoulvdberge.refinedstorage.network.MessageProcessingPatternEncoderTransfer;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeTransferHandlerPattern implements IRecipeTransferHandler<ContainerProcessingPatternEncoder> {
    @Override
    public Class<ContainerProcessingPatternEncoder> getContainerClass() {
        return ContainerProcessingPatternEncoder.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerProcessingPatternEncoder container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            Map<Integer, ItemStack> inputs = new LinkedHashMap<>();
            Map<Integer, ItemStack> outputs = new LinkedHashMap<>();

            for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                    ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();

                    int hash = API.instance().getItemStackHashCode(ingredient);

                    if (guiIngredient.isInput()) {
                        if (inputs.containsKey(hash)) {
                            inputs.get(hash).grow(1);
                        } else {
                            inputs.put(hash, ingredient);
                        }
                    } else {
                        if (outputs.containsKey(hash)) {
                            outputs.get(hash).grow(1);
                        } else {
                            outputs.put(hash, ingredient);
                        }
                    }
                }
            }

            RS.INSTANCE.network.sendToServer(new MessageProcessingPatternEncoderTransfer(inputs.values(), outputs.values()));
        }

        return null;
    }
}
