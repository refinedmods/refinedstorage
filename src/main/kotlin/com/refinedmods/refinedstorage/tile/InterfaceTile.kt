package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.InterfaceNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class InterfaceTile : NetworkNodeTile<InterfaceNetworkNode?>(RSTiles.INTERFACE) {
    private val itemsCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.items })

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            itemsCapability.cast()
        } else super.getCapability<T>(cap, direction)
    }

    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): InterfaceNetworkNode {
        return InterfaceNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, InterfaceTile> = IComparable.Companion.createParameter()
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
    }
}