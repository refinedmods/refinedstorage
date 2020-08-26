package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.CrafterManagerContainer
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider
import com.refinedmods.refinedstorage.tile.CrafterManagerTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.IContainerFactory
import java.util.*

class CrafterManagerContainerFactory : IContainerFactory<CrafterManagerContainer?> {
    fun create(windowId: Int, inv: PlayerInventory, buf: PacketByteBuf): CrafterManagerContainer {
        val data: MutableMap<String, Int> = LinkedHashMap()
        val pos: BlockPos = buf.readBlockPos()
        val size: Int = buf.readInt()
        for (i in 0 until size) {
            data[buf.readTextComponent().getString()] = buf.readInt()
        }
        val container = CrafterManagerContainer(inv.player.world.getBlockEntity(pos) as CrafterManagerTile?, inv.player, windowId)
        container.setScreenInfoProvider(EmptyScreenInfoProvider())
        container.initSlots(data)
        return container
    }
}