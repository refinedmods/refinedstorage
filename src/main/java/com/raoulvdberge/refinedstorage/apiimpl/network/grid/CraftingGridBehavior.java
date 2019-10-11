package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.ICraftingGridBehavior;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridNetworkAware;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class CraftingGridBehavior implements ICraftingGridBehavior {
    @Override
    public void onCrafted(IGridNetworkAware grid, ICraftingRecipe recipe, PlayerEntity player) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(grid.getCraftingMatrix());

        INetwork network = grid.getNetwork();

        CraftingInventory matrix = grid.getCraftingMatrix();

        for (int i = 0; i < grid.getCraftingMatrix().getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            // Do we have a remainder?
            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                // If there is no space for the remainder, dump it in the player inventory.
                if (!slot.isEmpty() && slot.getCount() > 1) {
                    if (!player.inventory.addItemStackToInventory(remainder.get(i).copy())) { // If there is no space in the player inventory, try to dump it in the network.
                        ItemStack remainderStack = network == null ? remainder.get(i).copy() : network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(), Action.PERFORM);

                        // If there is no space in the network, just dump it in the world.
                        if (remainderStack != null) {
                            InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainderStack);
                        }
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, remainder.get(i).copy());
                }
            } else if (!slot.isEmpty()) { // We don't have a remainder, but the slot is not empty.
                if (slot.getCount() == 1 && network != null) { // Attempt to refill the slot with the same item from the network, only if we have a network and only if it's the last item.
                    ItemStack refill = StackUtils.nullToEmpty(network.extractItem(slot, 1, Action.PERFORM));

                    matrix.setInventorySlotContents(i, refill);

                    if (!refill.isEmpty()) {
                        network.getItemStorageTracker().changed(player, refill.copy());
                    }
                } else { // We don't have a network, or, the slot still has more than 1 items in it. Just decrement then.
                    matrix.decrStackSize(i, 1);
                }
            }
        }

        grid.onCraftingMatrixChanged();
    }

    @Override
    public void onCraftedShift(IGridNetworkAware grid, PlayerEntity player) {
        List<ItemStack> craftedItemsList = new ArrayList<>();
        int amountCrafted = 0;
        ItemStack crafted = grid.getCraftingResult().getStackInSlot(0);

        // Do while the item is still craftable (aka is the result slot still the same as the original item?) and we don't exceed the max stack size.
        do {
            grid.onCrafted(player);

            craftedItemsList.add(crafted.copy());

            amountCrafted += crafted.getCount();
        } while (API.instance().getComparer().isEqual(crafted, grid.getCraftingResult().getStackInSlot(0)) && amountCrafted + crafted.getCount() < crafted.getMaxStackSize());

        INetwork network = grid.getNetwork();

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                ItemStack remainder = network == null ? craftedItem : network.insertItem(craftedItem, craftedItem.getCount(), Action.PERFORM);

                if (remainder != null) {
                    InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainder);
                }
            }
        }

        // @Volatile: This is some logic copied from CraftingResultSlot#onCrafting. We call this manually for shift clicking because
        // otherwise it's not being called.
        // For regular crafting, this is already called in ResultCraftingGridSlot#onTake -> onCrafting(stack)
        crafted.onCrafting(player.world, player, amountCrafted);
        BasicEventHooks.firePlayerCraftingEvent(player, ItemHandlerHelper.copyStackWithSize(crafted, amountCrafted), grid.getCraftingMatrix());
    }
}
