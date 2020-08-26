package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

class DestructorTile : NetworkNodeTile<DestructorNetworkNode>(RSTiles.DESTRUCTOR) {

    override fun createNode(world: World, pos: BlockPos): DestructorNetworkNode = DestructorNetworkNode(world, pos)

    companion object {
        val COMPARE: TileDataParameter<Int, DestructorTile> = IComparable.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, DestructorTile> = IWhitelistBlacklist.createParameter()
        val TYPE: TileDataParameter<Int, DestructorTile> = IType.createParameter()
        val PICKUP = TileDataParameter(
                DataSerializers.BOOLEAN,
                false,
                Function { t: DestructorTile -> t.node.isPickupItem },
                BiConsumer<DestructorTile, Boolean> { t: DestructorTile, v: Boolean? ->
                    t.node.isPickupItem = v!!
                    t.node.markDirty()
                }
        )
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(PICKUP)
    }
}