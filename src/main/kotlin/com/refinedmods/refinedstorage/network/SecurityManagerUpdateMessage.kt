package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.tile.SecurityManagerTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SecurityManagerUpdateMessage(private val pos: BlockPos, private val permission: Permission, private val state: Boolean) {
    companion object {
        fun decode(buf: PacketByteBuf): SecurityManagerUpdateMessage {
            val pos: BlockPos = buf.readBlockPos()
            val id: Int = buf.readInt()
            var permission = Permission.INSERT
            for (otherPermission in Permission.values()) {
                if (otherPermission.id == id) {
                    permission = otherPermission
                    break
                }
            }
            val state: Boolean = buf.readBoolean()
            return SecurityManagerUpdateMessage(pos, permission, state)
        }

        fun encode(message: SecurityManagerUpdateMessage, buf: PacketByteBuf) {
            buf.writeBlockPos(message.pos)
            buf.writeInt(message.permission.id)
            buf.writeBoolean(message.state)
        }

        fun handle(message: SecurityManagerUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val tile: BlockEntity? = player.entityWorld.getBlockEntity(message.pos)
                    if (tile is SecurityManagerTile) {
                        (tile as SecurityManagerTile?).getNode().updatePermission(message.permission, message.state)
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}