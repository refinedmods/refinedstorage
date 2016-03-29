package refinedstorage.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.IGuiIngredient;
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

// Thanks to https://github.com/zerofall/EZStorage/blob/master/src/main/java/com/zerofall/ezstorage/jei/RecipeTransferHandler.java
public class GridRecipeTransferHandler implements IRecipeTransferHandler {
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

                        for (ItemStack stack : possibleItems) {
                            NBTTagCompound tag = new NBTTagCompound();
                            stack.writeToNBT(tag);
                            tags.appendTag(tag);
                        }

                        recipe.setTag("#" + slot.getSlotIndex(), tags);
                    }
                }
            }

            RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingTransfer(recipe));
        }

        return null;
    }
}
