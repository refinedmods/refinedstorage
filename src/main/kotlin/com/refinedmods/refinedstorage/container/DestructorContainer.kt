package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.DestructorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class DestructorContainer(destructor: DestructorTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.DESTRUCTOR, destructor, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(destructor.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(destructor.getNode().getItemFilters(), i, 8 + 18 * i, 20).setEnableHandler { destructor.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(destructor.getNode().getFluidFilters(), i, 8 + 18 * i, 20).setEnableHandler { destructor.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, destructor.getNode().getUpgrades())
        transferManager.addFilterTransfer(player.inventory, destructor.getNode().getItemFilters(), destructor.getNode().getFluidFilters(), Supplier { destructor.getNode().getType() })
    }
}