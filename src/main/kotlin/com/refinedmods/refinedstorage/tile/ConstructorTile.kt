package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.extensions.PacketIO
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

class ConstructorTile : NetworkNodeTile<ConstructorNetworkNode>(RSTiles.CONSTRUCTOR) {

    override fun createNode(world: World, pos: BlockPos): ConstructorNetworkNode {
        return ConstructorNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, ConstructorTile> = IComparable.createParameter()
        val TYPE: TileDataParameter<Int, ConstructorTile> = IType.createParameter()
        val IO: PacketIO
        val DROP = TileDataParameter(
                PacketIO{

                }
                DataSerializers.BOOLEAN,
                false,
                Function { t: ConstructorTile -> t.node.isDrop },
                BiConsumer<ConstructorTile, Boolean> { t: ConstructorTile, v: Boolean? ->
                    t.node.isDrop = v!!
                    t.node.markDirty()
                }
        )
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(DROP)
    }
}