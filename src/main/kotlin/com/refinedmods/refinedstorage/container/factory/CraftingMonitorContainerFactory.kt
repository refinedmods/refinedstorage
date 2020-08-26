package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.IContainerFactory

class CraftingMonitorContainerFactory : IContainerFactory<CraftingMonitorContainer?> {
    fun create(windowId: Int, inv: PlayerInventory, data: PacketByteBuf): CraftingMonitorContainer {
        val pos: BlockPos = data.readBlockPos()
        val tile = inv.player.world.getBlockEntity(pos) as CraftingMonitorTile?
        return CraftingMonitorContainer(RSContainers.CRAFTING_MONITOR, tile.getNode(), tile, inv.player, windowId)
    }
}