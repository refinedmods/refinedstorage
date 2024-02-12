package com.refinedmods.refinedstorage.apiimpl.network.grid;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridBehavior;
import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CraftingGridBehavior implements ICraftingGridBehavior {
    @Override
    public void onCrafted(INetworkAwareGrid grid, CraftingRecipe recipe, Player player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(grid.getCraftingMatrix());

        INetwork network = grid.getNetwork();

        CraftingContainer matrix = grid.getCraftingMatrix();

        for (int i = 0; i < grid.getCraftingMatrix().getContainerSize(); ++i) {
            ItemStack slot = matrix.getItem(i);

            // Do we have a remainder?
            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                // If there is no space for the remainder, dump it in the player inventory.
                if (!slot.isEmpty() && slot.getCount() > 1) {
                    if (!player.getInventory().add(remainder.get(i).copy())) { // If there is no space in the player inventory, try to dump it in the network.
                        ItemStack remainderStack = network == null ? remainder.get(i).copy() : network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(), Action.PERFORM);

                        // If there is no space in the network, just dump it in the world.
                        if (!remainderStack.isEmpty()) {
                            Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), remainderStack);
                        }
                    }

                    matrix.removeItem(i, 1);
                } else {
                    matrix.setItem(i, remainder.get(i).copy());
                }
            } else if (!slot.isEmpty()) { // We don't have a remainder, but the slot is not empty.
                if (slot.getCount() == 1 && network != null && grid.isGridActive()) { // Attempt to refill the slot with the same item from the network, only if we have a network and only if it's the last item.
                    ItemStack refill;
                    if (availableItems == null) { // for regular crafting
                        refill = network.extractItem(slot, 1, Action.PERFORM);
                    } else { // for shift crafting
                        if (availableItems.get(slot) != null) {
                            refill = availableItems.remove(slot, 1).getStack().copy();
                            refill.setCount(1);
                            usedItems.add(refill);
                        } else {
                            refill = ItemStack.EMPTY;
                        }
                    }

                    matrix.setItem(i, refill);

                    if (!refill.isEmpty()) {
                        network.getItemStorageTracker().changed(player, refill.copy());
                    }
                } else { // We don't have a network, or, the slot still has more than 1 items in it. Just decrement then.
                    matrix.removeItem(i, 1);
                }
            }
        }

        grid.onCraftingMatrixChanged();
    }

    @Override
    public void onCraftedShift(INetworkAwareGrid grid, Player player) {
        CraftingContainer matrix = grid.getCraftingMatrix();
        INetwork network = grid.getNetwork();
        List<ItemStack> craftedItemsList = new ArrayList<>();
        ItemStack crafted = grid.getCraftingResult().getItem(0);

        int maxCrafted = crafted.getMaxStackSize();

        int amountCrafted = 0;
        boolean useNetwork = network != null && grid.isGridActive();

        IStackList<ItemStack> availableItems = API.instance().createItemStackList();
        if (useNetwork) {
            // We need a modifiable list of the items in storage that are relevant for this craft.
            // For performance reason we extract these into an extra list
            filterDuplicateStacks(network, matrix, availableItems);
        }

        //A second list to remember which items have been extracted
        IStackList<ItemStack> usedItems = API.instance().createItemStackList();

        CommonHooks.setCraftingPlayer(player);
        // Do while the item is still craftable (aka is the result slot still the same as the original item?) and we don't exceed the max stack size.
        do {
            grid.onCrafted(player, availableItems, usedItems);

            craftedItemsList.add(crafted.copy());

            amountCrafted += crafted.getCount();
        } while (API.instance().getComparer().isEqual(crafted, grid.getCraftingResult().getItem(0)) && amountCrafted < maxCrafted && amountCrafted + crafted.getCount() <= maxCrafted);

        if (useNetwork) {
            usedItems.getStacks().forEach(stack -> network.extractItem(stack.getStack(), stack.getStack().getCount(), Action.PERFORM));
        }

        for (ItemStack craftedItem : craftedItemsList) {
            ItemStack remainder = ItemHandlerHelper.insertItem(
                    new PlayerMainInvWrapper(player.getInventory()),
                    craftedItem.copy(),
                    false
            );

            if (!remainder.isEmpty() && useNetwork) {
                remainder = network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
            }

            if (!remainder.isEmpty()) {
                Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), remainder);
            }
        }

        // @Volatile: This is some logic copied from ResultSlot#checkTakeAchievements. We call this manually for shift clicking because
        // otherwise it's not being called.
        // For regular crafting, this is already called in ResultCraftingGridSlot#onTake -> checkTakeAchievements(stack)
        crafted.onCraftedBy(player.level(), player, amountCrafted);
        EventHooks.firePlayerCraftingEvent(player, ItemHandlerHelper.copyStackWithSize(crafted, amountCrafted), grid.getCraftingMatrix());
        CommonHooks.setCraftingPlayer(null);
    }

    private void filterDuplicateStacks(INetwork network, CraftingContainer matrix, IStackList<ItemStack> availableItems) {
        for (int i = 0; i < matrix.getContainerSize(); ++i) {
            ItemStack stack = network.getItemStorageCache().getList().get(matrix.getItem(i));

            //Don't add the same item twice into the list. Items may appear twice in a recipe but not in storage.
            if (stack != null && availableItems.get(stack) == null) {
                availableItems.add(stack);
            }
        }
    }

    @Override
    public void onRecipeTransfer(INetworkAwareGrid grid, Player player, ItemStack[][] recipe) {
        INetwork network = grid.getNetwork();

        if (network != null && grid.getGridType() == GridType.CRAFTING && !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        // First try to empty the crafting matrix
        for (int i = 0; i < grid.getCraftingMatrix().getContainerSize(); ++i) {
            ItemStack slot = grid.getCraftingMatrix().getItem(i);

            if (!slot.isEmpty()) {
                // Only if we are a crafting grid. Pattern grids can just be emptied.
                if (grid.getGridType() == GridType.CRAFTING) {
                    // If we are connected, try to insert into network. If it fails, stop.
                    if (network != null && grid.isGridActive()) {
                        if (!network.insertItem(slot, slot.getCount(), Action.SIMULATE).isEmpty()) {
                            return;
                        } else {
                            network.insertItem(slot, slot.getCount(), Action.PERFORM);

                            network.getItemStorageTracker().changed(player, slot.copy());
                        }
                    } else {
                        // If we aren't connected, try to insert into player inventory. If it fails, stop.
                        if (!player.getInventory().add(slot.copy())) {
                            return;
                        }
                    }
                }

                grid.getCraftingMatrix().setItem(i, ItemStack.EMPTY);
            }
        }

        AtomicReference<Map<Item, ItemStack>> playerItems = new AtomicReference<>();
        // Now let's fill the matrix
        for (int i = 0; i < grid.getCraftingMatrix().getContainerSize(); ++i) {
            if (recipe[i] != null) {
                ItemStack[] possibilities = recipe[i];

                if (network != null && grid.isGridActive() && network.getItemStorageCache() != null) {

                    // sort by the number of items in storage, craftables and inventory
                    Arrays.sort(possibilities, compareByItemStackCounts(player, network, playerItems));
                }

                // If we are a crafting grid
                if (grid.getGridType() == GridType.CRAFTING) {
                    boolean found = false;

                    // If we are connected, first try to get the possibilities from the network
                    if (network != null && grid.isGridActive()) {
                        for (ItemStack possibility : possibilities) {
                            ItemStack took = network.extractItem(possibility, possibility.getCount(), IComparer.COMPARE_NBT, Action.PERFORM);

                            if (!took.isEmpty()) {
                                grid.getCraftingMatrix().setItem(i, took);

                                network.getItemStorageTracker().changed(player, took.copy());

                                found = true;

                                break;
                            }
                        }
                    }

                    // If we haven't found anything in the network (or we are disconnected), go look in the player inventory
                    if (!found) {
                        for (ItemStack possibility : possibilities) {
                            for (int j = 0; j < player.getInventory().getContainerSize(); ++j) {
                                if (API.instance().getComparer().isEqual(possibility, player.getInventory().getItem(j), IComparer.COMPARE_NBT)) {
                                    grid.getCraftingMatrix().setItem(i, ItemHandlerHelper.copyStackWithSize(player.getInventory().getItem(j), 1));

                                    player.getInventory().removeItem(j, 1);

                                    found = true;

                                    break;
                                }
                            }

                            if (found) {
                                break;
                            }
                        }
                    }
                } else if (grid.getGridType() == GridType.PATTERN) {
                    // If we are a pattern grid we can just set the slot
                    grid.getCraftingMatrix().setItem(i, possibilities.length == 0 ? ItemStack.EMPTY : possibilities[0]);
                }
            }
        }

        if (grid.getGridType() == GridType.PATTERN) {
            ((GridNetworkNode) grid).setProcessingPattern(false);
            ((GridNetworkNode) grid).markDirty();
        }
    }

    private Comparator<ItemStack> compareByItemStackCounts(Player player, INetwork network, AtomicReference<Map<Item, ItemStack>> playerItems) {
        return Comparator.comparingInt((ItemStack itemStack) -> {

            ItemStack stack = network.getItemStorageCache().getList().get(itemStack);
            if (stack != null) {
                return stack.getCount();
            }

            if (network.getCraftingManager().getPattern(itemStack) != null) {
                return 1;
            }

            if (playerItems.get() == null) {
                playerItems.set(makePlayerInventoryMap(player, network));
            }

            ItemStack onPlayer = playerItems.get().get(itemStack.getItem());
            if (onPlayer != null) {
                return onPlayer.getCount();
            }
            return 0;
        }).reversed();
    }

    private Map<Item, ItemStack> makePlayerInventoryMap(Player player, INetwork network) {
        Map<Item, ItemStack> playerItems = new HashMap<>();
        for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
            ItemStack inventoryStack = player.getInventory().getItem(j);

            if (inventoryStack.getItem() instanceof ICraftingPatternProvider) {
                ICraftingPattern pattern = PatternItem.fromCache(network.getLevel(), inventoryStack);
                if (pattern.isValid()) {
                    for (ItemStack stack : pattern.getOutputs()) {
                        if (!stack.isEmpty()) {
                            playerItems.put(stack.getItem(), stack);
                        }
                    }
                }
            } else {
                playerItems.put(inventoryStack.getItem(), inventoryStack);
            }
        }

        return playerItems;
    }

}
