package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.tile.grid.GridTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class GridPatternCreateMessage(private val pos: BlockPos) {
    companion object {
        fun decode(buf: PacketByteBuf): GridPatternCreateMessage {
            return GridPatternCreateMessage(buf.readBlockPos())
        }

        fun encode(message: GridPatternCreateMessage, buf: PacketByteBuf) {
            buf.writeBlockPos(message.pos)
        }

        fun handle(message: GridPatternCreateMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val tile: BlockEntity? = player.entityWorld.getBlockEntity(message.pos)
                    if (tile is GridTile && (tile as GridTile?).getNode().gridType === GridType.PATTERN) {
                        (tile as GridTile?).getNode().onCreatePattern()
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}