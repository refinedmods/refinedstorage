package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.apiimpl.network.NetworkManager
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket

abstract class BaseTile(tileType: BlockEntityType<*>?):
        BlockEntity(tileType)
{
    val dataManager by lazy { TileDataManager(this) }
    fun writeUpdate(tag: CompoundTag): CompoundTag {
        return tag
    }

    fun readUpdate(tag: CompoundTag) {}
//    val updateTag: CompoundTag
//        get() = writeUpdate(super.toUpdatePacket()?.compoundTag)
//
//    fun onDataPacket(net: NetworkManager, packet: SUpdateBlockEntityPacket) {
//        readUpdate(packet.getNbtCompound())
//    }
//
//    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket {
//        return BlockEntityUpdateS2CPacket(pos, 1, updateTag)
//    }

    fun handleUpdateTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        readUpdate(tag)
    }

    override fun markDirty() {
        world?.markDirty(pos, this)
    }
}