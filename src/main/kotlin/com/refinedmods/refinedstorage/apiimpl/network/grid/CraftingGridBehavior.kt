package com.refinedmods.refinedstorage.apiimpl.network.grid

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridBehavior
import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ICraftingRecipe
import net.minecraft.util.NonNullList
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.fml.hooks.BasicEventHooks
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*


class CraftingGridBehavior : ICraftingGridBehavior {
    override fun onCrafted(grid: INetworkAwareGrid?, recipe: ICraftingRecipe?, player: PlayerEntity?, @Nullable availableItems: IStackList<ItemStack?>?, @Nullable usedItems: IStackList<ItemStack?>?) {
        val remainder: NonNullList<ItemStack> = recipe.getRemainingItems(grid!!.craftingMatrix)
        val network = grid.network
        val matrix = grid.craftingMatrix
        for (i in 0 until grid.craftingMatrix.getSizeInventory()) {
            val slot: ItemStack = matrix.getStackInSlot(i)

            // Do we have a remainder?
            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                // If there is no space for the remainder, dump it in the player inventory.
                if (!slot.isEmpty && slot.count > 1) {
                    if (!player!!.inventory.addItemStackToInventory(remainder.get(i).copy())) { // If there is no space in the player inventory, try to dump it in the network.
                        val remainderStack = if (network == null) remainder.get(i).copy() else network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(), Action.PERFORM)!!

                        // If there is no space in the network, just dump it in the world.
                        if (!remainderStack.isEmpty) {
                            InventoryHelper.spawnItemStack(player.entityWorld, player.getPosX(), player.getPosY(), player.getPosZ(), remainderStack)
                        }
                    }
                    matrix.decrStackSize(i, 1)
                } else {
                    matrix.setInventorySlotContents(i, remainder.get(i).copy())
                }
            } else if (!slot.isEmpty) { // We don't have a remainder, but the slot is not empty.
                if (slot.count == 1 && network != null) { // Attempt to refill the slot with the same item from the network, only if we have a network and only if it's the last item.
                    var refill: ItemStack?
                    if (availableItems == null) { // for regular crafting
                        refill = network.extractItem(slot, 1, Action.PERFORM)
                    } else { // for shift crafting
                        if (availableItems[slot] != null) {
                            refill = availableItems.remove(slot, 1)!!.stack!!.copy()
                            refill.count = 1
                            usedItems!!.add(refill)
                        } else {
                            refill = ItemStack.EMPTY
                        }
                    }
                    matrix.setInventorySlotContents(i, refill)
                    if (!refill!!.isEmpty) {
                        network.itemStorageTracker!!.changed(player, refill.copy())
                    }
                } else { // We don't have a network, or, the slot still has more than 1 items in it. Just decrement then.
                    matrix.decrStackSize(i, 1)
                }
            }
        }
        grid.onCraftingMatrixChanged()
    }

    override fun onCraftedShift(grid: INetworkAwareGrid?, player: PlayerEntity?) {
        val matrix = grid!!.craftingMatrix
        val network = grid.network
        val craftedItemsList: MutableList<ItemStack> = ArrayList()
        val crafted: ItemStack = grid.craftingResult.getStackInSlot(0)
        val maxCrafted: Int = crafted.getMaxStackSize()
        var amountCrafted = 0
        val useNetwork = network != null
        var availableItems: IStackList<ItemStack?>? = null
        if (useNetwork) {
            // We need a modifiable list of the items in storage that are relevant for this craft.
            // For performance reason we extract these into an extra list
            availableItems = createFilteredItemList(network, matrix)
        }

        //A second list to remember which items have been extracted
        val usedItems = instance().createItemStackList()
        ForgeHooks.setCraftingPlayer(player)
        // Do while the item is still craftable (aka is the result slot still the same as the original item?) and we don't exceed the max stack size.
        do {
            grid.onCrafted(player, availableItems, usedItems)
            craftedItemsList.add(crafted.copy())
            amountCrafted += crafted.count
        } while (instance().getComparer()!!.isEqual(crafted, grid.craftingResult.getStackInSlot(0)) && amountCrafted < maxCrafted && amountCrafted + crafted.count <= maxCrafted)
        if (useNetwork) {
            usedItems.getStacks().forEach { stack -> network!!.extractItem(stack.getStack(), stack.getStack().getCount(), Action.PERFORM) }
        }
        for (craftedItem in craftedItemsList) {
            if (!player!!.inventory.addItemStackToInventory(craftedItem.copy())) {
                var remainder: ItemStack? = craftedItem
                if (useNetwork) {
                    remainder = network!!.insertItem(craftedItem, craftedItem.count, Action.PERFORM)
                }
                if (!remainder!!.isEmpty) {
                    InventoryHelper.spawnItemStack(player.entityWorld, player.getPosX(), player.getPosY(), player.getPosZ(), remainder)
                }
            }
        }

        // @Volatile: This is some logic copied from CraftingResultSlot#onCrafting. We call this manually for shift clicking because
        // otherwise it's not being called.
        // For regular crafting, this is already called in ResultCraftingGridSlot#onTake -> onCrafting(stack)
        crafted.onCrafting(player!!.world, player, amountCrafted)
        BasicEventHooks.firePlayerCraftingEvent(player, ItemHandlerHelper.copyStackWithSize(crafted, amountCrafted), grid.craftingMatrix)
        ForgeHooks.setCraftingPlayer(null)
    }

    private fun createFilteredItemList(network: INetwork?, matrix: CraftingInventory?): IStackList<ItemStack?>? {
        val availableItems = instance().createItemStackList()
        for (i in 0 until matrix.getSizeInventory()) {
            val stack: ItemStack = network!!.itemStorageCache!!.getList().get(matrix.getStackInSlot(i))

            //Don't add the same item twice into the list. Items may appear twice in a recipe but not in storage.
            if (stack != null && availableItems!![stack] == null) {
                availableItems.add(stack)
            }
        }
        return availableItems
    }

    override fun onRecipeTransfer(grid: INetworkAwareGrid?, player: PlayerEntity?, recipe: Array<Array<ItemStack?>?>?) {
        val network = grid!!.network
        if (network != null && grid.gridType === GridType.CRAFTING && !network.securityManager!!.hasPermission(Permission.EXTRACT, player)) {
            return
        }

        // First try to empty the crafting matrix
        for (i in 0 until grid.craftingMatrix.getSizeInventory()) {
            val slot: ItemStack = grid.craftingMatrix.getStackInSlot(i)
            if (!slot.isEmpty) {
                // Only if we are a crafting grid. Pattern grids can just be emptied.
                if (grid.gridType === GridType.CRAFTING) {
                    // If we are connected, try to insert into network. If it fails, stop.
                    if (network != null) {
                        if (!network.insertItem(slot, slot.count, Action.SIMULATE)!!.isEmpty) {
                            return
                        } else {
                            network.insertItem(slot, slot.count, Action.PERFORM)
                            network.itemStorageTracker!!.changed(player, slot.copy())
                        }
                    } else {
                        // If we aren't connected, try to insert into player inventory. If it fails, stop.
                        if (!player!!.inventory.addItemStackToInventory(slot.copy())) {
                            return
                        }
                    }
                }
                grid.craftingMatrix.setInventorySlotContents(i, ItemStack.EMPTY)
            }
        }

        // Now let's fill the matrix
        for (i in 0 until grid.craftingMatrix.getSizeInventory()) {
            if (recipe!![i] != null) {
                val possibilities = recipe[i]

                // If we are a crafting grid
                if (grid.gridType === GridType.CRAFTING) {
                    var found = false

                    // If we are connected, first try to get the possibilities from the network
                    if (network != null) {
                        for (possibility in possibilities!!) {
                            val took = network.extractItem(possibility, 1, IComparer.COMPARE_NBT, Action.PERFORM)
                            if (!took!!.isEmpty) {
                                grid.craftingMatrix.setInventorySlotContents(i, took)
                                network.itemStorageTracker!!.changed(player, took.copy())
                                found = true
                                break
                            }
                        }
                    }

                    // If we haven't found anything in the network (or we are disconnected), go look in the player inventory
                    if (!found) {
                        for (possibility in possibilities!!) {
                            for (j in 0 until player!!.inventory.getSizeInventory()) {
                                if (instance().getComparer()!!.isEqual(possibility!!, player.inventory.getStackInSlot(j), IComparer.COMPARE_NBT)) {
                                    grid.craftingMatrix.setInventorySlotContents(i, ItemHandlerHelper.copyStackWithSize(player.inventory.getStackInSlot(j), 1))
                                    player.inventory.decrStackSize(j, 1)
                                    found = true
                                    break
                                }
                            }
                            if (found) {
                                break
                            }
                        }
                    }
                } else if (grid.gridType === GridType.PATTERN) {
                    // If we are a pattern grid we can just set the slot
                    grid.craftingMatrix.setInventorySlotContents(i, if (possibilities!!.size == 0) ItemStack.EMPTY else possibilities[0])
                }
            }
        }
        if (grid.gridType === GridType.PATTERN) {
            (grid as GridNetworkNode?)!!.isProcessingPattern = false
            (grid as GridNetworkNode?)!!.markDirty()
        }
    }
}