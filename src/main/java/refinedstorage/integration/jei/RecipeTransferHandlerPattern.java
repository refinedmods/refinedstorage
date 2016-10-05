package refinedstorage.integration.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.container.ContainerProcessingPatternEncoder;
import refinedstorage.network.MessageProcessingPatternEncoderTransfer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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
            Map<Integer, ItemStack> inputs = new HashMap<>();
            Map<Integer, ItemStack> outputs = new HashMap<>();

            for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                    ItemStack ingredient = guiIngredient.getDisplayedIngredient();

                    int hash = NetworkUtils.getItemStackHashCode(ingredient);

                    if (guiIngredient.isInput()) {
                        if (inputs.containsKey(hash)) {
                            inputs.get(hash).stackSize++;
                        } else {
                            inputs.put(hash, ingredient);
                        }
                    } else {
                        if (outputs.containsKey(hash)) {
                            outputs.get(hash).stackSize++;
                        } else {
                            outputs.put(hash, ingredient);
                        }
                    }
                }
            }

            RefinedStorage.INSTANCE.network.sendToServer(new MessageProcessingPatternEncoderTransfer(inputs.values(), outputs.values()));
        }

        return null;
    }
}
