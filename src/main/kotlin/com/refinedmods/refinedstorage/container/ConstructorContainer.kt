package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class ConstructorContainer(constructor: ConstructorTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.CONSTRUCTOR, constructor, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(constructor.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        addSlot(FilterSlot(constructor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler { constructor.getNode().getType() == IType.ITEMS })
        addSlot(FluidFilterSlot(constructor.getNode().getFluidFilters(), 0, 80, 20, 0).setEnableHandler { constructor.getNode().getType() == IType.FLUIDS })
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, constructor.getNode().getUpgrades())
        transferManager.addFilterTransfer(player.inventory, constructor.getNode().getItemFilters(), constructor.getNode().getFluidFilters(), Supplier { constructor.getNode().getType() })
    }
}