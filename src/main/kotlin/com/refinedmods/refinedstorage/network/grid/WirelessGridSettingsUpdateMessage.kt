package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSearchBoxMode
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSize
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingDirection
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingType
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidViewType
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.tile.grid.WirelessGrid
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class WirelessGridSettingsUpdateMessage(private val viewType: Int, private val sortingDirection: Int, private val sortingType: Int, private val searchBoxMode: Int, private val size: Int, private val tabSelected: Int, private val tabPage: Int) {
    companion object {
        fun decode(buf: PacketByteBuf): WirelessGridSettingsUpdateMessage {
            return WirelessGridSettingsUpdateMessage(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
            )
        }

        fun encode(message: WirelessGridSettingsUpdateMessage, buf: PacketByteBuf) {
            buf.writeInt(message.viewType)
            buf.writeInt(message.sortingDirection)
            buf.writeInt(message.sortingType)
            buf.writeInt(message.searchBoxMode)
            buf.writeInt(message.size)
            buf.writeInt(message.tabSelected)
            buf.writeInt(message.tabPage)
        }

        fun handle(message: WirelessGridSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    if (player.openContainer is GridContainer) {
                        val grid = (player.openContainer as GridContainer).grid
                        if (grid is WirelessGrid) {
                            val stack = grid.stack
                            if (!stack.hasTag()) {
                                stack.tag = CompoundTag()
                            }
                            if (isValidViewType(message.viewType)) {
                                stack.tag!!.putInt(GridNetworkNode.NBT_VIEW_TYPE, message.viewType)
                            }
                            if (isValidSortingDirection(message.sortingDirection)) {
                                stack.tag!!.putInt(GridNetworkNode.NBT_SORTING_DIRECTION, message.sortingDirection)
                            }
                            if (isValidSortingType(message.sortingType)) {
                                stack.tag!!.putInt(GridNetworkNode.NBT_SORTING_TYPE, message.sortingType)
                            }
                            if (isValidSearchBoxMode(message.searchBoxMode)) {
                                stack.tag!!.putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, message.searchBoxMode)
                            }
                            if (isValidSize(message.size)) {
                                stack.tag!!.putInt(GridNetworkNode.NBT_SIZE, message.size)
                            }
                            stack.tag!!.putInt(GridNetworkNode.NBT_TAB_SELECTED, message.tabSelected)
                            stack.tag!!.putInt(GridNetworkNode.NBT_TAB_PAGE, message.tabPage)
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}