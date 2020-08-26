package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider
import com.refinedmods.refinedstorage.tile.BaseTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.text.Text

class GridContainerProvider(private val grid: IGrid, tile: BlockEntity) : INamedContainerProvider {
    private val tile: BlockEntity
    val displayName: Text?
        get() = grid.title

    @Nullable
    fun createMenu(windowId: Int, inv: PlayerInventory?, player: PlayerEntity): Container {
        val c = GridContainer(grid, if (tile is BaseTile) tile else null, player, windowId)
        c.setScreenInfoProvider(EmptyScreenInfoProvider())
        c.initSlots()
        return c
    }

    init {
        this.tile = tile
    }
}