package com.refinedmods.refinedstorage.container.slot.grid

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.CraftingResultSlot
import net.minecraft.item.ItemStack

class ResultCraftingGridSlot(player: PlayerEntity?, private val grid: IGrid?, inventoryIndex: Int, x: Int, y: Int) : CraftingResultSlot(player, grid!!.craftingMatrix, grid!!.craftingResult, inventoryIndex, x, y) {
    // @Volatile: Overriding logic from the super onTake method for Grid behaviors like refilling stacks from the network
    @Nonnull
    fun onTake(player: PlayerEntity, @Nonnull stack: ItemStack?): ItemStack {
        onCrafting(stack)
        if (!player.entityWorld.isRemote) {
            grid!!.onCrafted(player, null, null)
        }
        return ItemStack.EMPTY
    }
}