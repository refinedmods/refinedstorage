package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.CrafterManagerContainer
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider
import com.refinedmods.refinedstorage.tile.CrafterManagerTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandlerModifiable

class CrafterManagerContainerProvider(private val tile: CrafterManagerTile) : INamedContainerProvider {
    val displayName: Text
        get() = TranslationTextComponent("gui.refinedstorage.crafter_manager")

    @Nullable
    fun createMenu(windowId: Int, playerInventory: PlayerInventory?, playerEntity: PlayerEntity): Container {
        val container = CrafterManagerContainer(tile, playerEntity, windowId)
        container.setScreenInfoProvider(EmptyScreenInfoProvider())
        container.initSlotsServer()
        return container
    }

    companion object {
        fun writeToBuffer(buf: PacketByteBuf, world: World, pos: BlockPos?) {
            buf.writeBlockPos(pos)
            val containerData: Map<Text?, List<IItemHandlerModifiable>> = (world.getBlockEntity(pos) as CrafterManagerTile?).getNode().network.craftingManager.getNamedContainers()
            buf.writeInt(containerData.size)
            for ((key, value) in containerData) {
                buf.writeTextComponent(key)
                var slots = 0
                for (handler in value) {
                    slots += handler.getSlots()
                }
                buf.writeInt(slots)
            }
        }
    }
}