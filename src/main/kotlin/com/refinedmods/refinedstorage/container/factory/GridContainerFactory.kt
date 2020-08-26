package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.tile.BaseTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.IContainerFactory
import org.apache.commons.lang3.tuple.Pair

class GridContainerFactory : IContainerFactory<GridContainer?> {
    fun create(windowId: Int, inv: PlayerInventory, data: PacketByteBuf): GridContainer {
        val id: Identifier = data.readIdentifier()
        var pos: BlockPos? = null
        var stack: ItemStack? = null
        if (data.readBoolean()) {
            pos = data.readBlockPos()
        }
        if (data.readBoolean()) {
            stack = data.readItemStack()
        }
        val slotId: Int = data.readInt()
        val grid: Pair<IGrid?, BlockEntity?>? = instance().getGridManager()!!.createGrid(id, inv.player, stack, pos, slotId)
        return GridContainer(grid!!.getLeft(), if (grid.getRight() is BaseTile) grid.getRight() else null, inv.player, windowId)
    }
}