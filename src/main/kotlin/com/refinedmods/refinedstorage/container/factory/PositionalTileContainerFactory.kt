package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.IContainerFactory

class PositionalTileContainerFactory<C : Container?, T : BlockEntity?>(factory: Factory<C, T>) : IContainerFactory<C> {
    interface Factory<C, T> {
        fun create(windowId: Int, inv: PlayerInventory?, tile: T): C
    }

    private val factory: Factory<C, T?>
    fun create(windowId: Int, inv: PlayerInventory, data: PacketByteBuf): C {
        val pos: BlockPos = data.readBlockPos()
        val tile = inv.player.world.getBlockEntity(pos) as T?
        return factory.create(windowId, inv, tile)
    }

    init {
        this.factory = factory
    }
}