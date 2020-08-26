package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.util.text.Text

class CraftingMonitorContainerProvider(containerType: ContainerType<CraftingMonitorContainer?>, craftingMonitor: ICraftingMonitor, @Nullable tile: CraftingMonitorTile) : INamedContainerProvider {
    private val craftingMonitor: ICraftingMonitor

    @Nullable
    private val tile: CraftingMonitorTile
    private val containerType: ContainerType<CraftingMonitorContainer?>
    val displayName: Text
        get() = craftingMonitor.title

    @Nullable
    fun createMenu(windowId: Int, playerInventory: PlayerInventory?, playerEntity: PlayerEntity): Container {
        return CraftingMonitorContainer(containerType, craftingMonitor, tile, playerEntity, windowId)
    }

    init {
        this.containerType = containerType
        this.craftingMonitor = craftingMonitor
        this.tile = tile
    }
}