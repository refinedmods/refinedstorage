package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Text

class PositionalTileContainerProvider<T : BlockEntity?>(name: Text, provider: Provider<T>, pos: BlockPos) : INamedContainerProvider {
    interface Provider<T> {
        fun create(tile: T, windowId: Int, inventory: PlayerInventory?, player: PlayerEntity?): Container
    }

    private val name: Text
    private val provider: Provider<T?>
    private val pos: BlockPos
    val displayName: Text
        get() = name

    @Nullable
    fun createMenu(windowId: Int, inventory: PlayerInventory?, player: PlayerEntity): Container {
        val tile = player.world.getBlockEntity(pos) as T?
        return provider.create(tile, windowId, inventory, player)
    }

    init {
        this.name = name
        this.provider = provider
        this.pos = pos
    }
}