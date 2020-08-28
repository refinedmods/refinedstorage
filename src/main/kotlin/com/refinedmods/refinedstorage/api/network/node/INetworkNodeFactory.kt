package com.refinedmods.refinedstorage.api.network.node

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


/**
 * A factory for reading network nodes from the disk. Used in a [INetworkNodeRegistry].
 */
interface INetworkNodeFactory {
    /**
     * Creates a network node.
     *
     * @param tag   the tag on disk
     * @param world the world
     * @param pos   the pos
     * @return the network node
     */
    fun create(tag: CompoundTag, world: World, pos: BlockPos): INetworkNode
}