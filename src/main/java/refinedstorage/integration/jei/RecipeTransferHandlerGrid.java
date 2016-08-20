package refinedstorage.integration.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerGrid;
import refinedstorage.network.MessageGridCraftingTransfer;

import java.util.List;
import java.util.Map;

/**
 * @link https://github.com/zerofall/EZStorage/blob/master/src/main/java/com/zerofall/ezstorage/jei/RecipeTransferHandler.java
 */
public class RecipeTransferHandlerGrid implements IRecipeTransferHandler {
    @Override
    public Class<? extends Container> getContainerClass() {
        return ContainerGrid.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return "minecraft.crafting";
    }

    @Override
    public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();

            NBTTagCompound recipe = new NBTTagCompound();

            for (Slot slot : container.inventorySlots) {
                if (slot.inventory instanceof InventoryCrafting) {
                    IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);

                    if (ingredient != null) {
                        List<ItemStack> possibleItems = ingredient.getAllIngredients();

                        NBTTagList tags = new NBTTagList();

                        for (int i = 0; i < possibleItems.size(); ++i) {
                            if (i >= 5) {
                                break; // Max 5 possible items to avoid reaching max network packet size
                            }

                            NBTTagCompound tag = new NBTTagCompound();
                            possibleItems.get(i).writeToNBT(tag);
                            tags.appendTag(tag);
                        }

                        recipe.setTag("#" + slot.getSlotIndex(), tags);
                    }
                }
            }

            RefinedStorage.INSTANCE.network.sendToServer(new MessageGridCraftingTransfer(recipe));
        }

        return null;
    }
}
