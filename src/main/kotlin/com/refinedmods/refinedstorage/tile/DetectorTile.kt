package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.DetectorScreen
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import com.refinedmods.refinedstorage.util.PacketByteBufUtils
import net.fabricmc.fabric.impl.networking.PacketTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketDeflater
import net.minecraft.util.JsonHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

class DetectorTile : NetworkNodeTile<DetectorNetworkNode>(RSTiles.DETECTOR) {
    override fun readUpdate(tag: CompoundTag) {
        node.isPowered = tag.getBoolean(NBT_POWERED)
        super.readUpdate(tag)
    }

    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        super.writeUpdate(tag)
        tag.putBoolean(NBT_POWERED, node.isPowered)
        return tag
    }
    
    override fun createNode(world: World?, pos: BlockPos?): DetectorNetworkNode {
        return DetectorNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, DetectorTile> = IComparable.Companion.createParameter()
        val TYPE: TileDataParameter<Int, DetectorTile> = IType.Companion.createParameter()
        val MODE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: DetectorTile -> t.node.mode }, BiConsumer { t: DetectorTile, v: Int ->
            if (v == DetectorNetworkNode.MODE_UNDER || v == DetectorNetworkNode.MODE_EQUAL || v == DetectorNetworkNode.MODE_ABOVE) {
                t.node.mode = v
                t.node.markDirty()
            }
        })
        val AMOUNT = TileDataParameter(DataSerializers.VARINT, 0, Function { t: DetectorTile -> t.node.amount }, BiConsumer<DetectorTile, Int> { t: DetectorTile, v: Int? ->
            t.node.amount = v!!
            t.node.markDirty()
        }, TileDataParameterClientListener<Int> { initial: Boolean, value: Int? -> BaseScreen.executeLater(DetectorScreen::class.java, { detectorScreen -> detectorScreen.updateAmountField(value) }) })
        private const val NBT_POWERED = "Powered"
    }

    init {
        PacketByteBufUtils
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(MODE)
        dataManager.addWatchedParameter(AMOUNT)
    }
}