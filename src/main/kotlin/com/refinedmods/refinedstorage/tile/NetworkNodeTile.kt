package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.createParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.lang.Exception

abstract class NetworkNodeTile<N : NetworkNode>(tileType: BlockEntityType<*>?):
        BaseTile(tileType),
        INetworkNodeProxy<N>,
        IRedstoneConfigurable
{

    companion object {
        @JvmField
        val REDSTONE_MODE: TileDataParameter<Int, NetworkNodeTile<*>> = createParameter()
        val log = getCustomLogger(NetworkNodeTile::class)
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
    }

    private var clientNode: N? = null
    var removedNode: N? = null
        private set
    private val networkNodeProxy: INetworkNodeProxy<N> by lazy { this }
    override var redstoneMode: RedstoneMode
        get() {
            return node.redstoneMode
        }
        set(value) {
            node.redstoneMode = value
        }

    @Suppress("UNCHECKED_CAST")
    override val node: N
        get() {

            return when (world!!.isClient) {
                true -> {
                    clientNode = clientNode ?: createNode(world!!, pos)
                    clientNode!! // Safe due to above null and set check
                }
                false -> {
                    try {
                        instance()
                                .getNetworkNodeManager(world as ServerWorld)
                                .getNode(pos)!! as N
                    } catch (e: Exception) {
                        log.warn("No valid network node present at $pos, consider removing the block at this position!")
                        throw e

                        // TODO Use markInvalid() instead rather than crashing
                    }
                }
            }
        }

    override fun markRemoved() {
        super.markRemoved()
        if (!world!!.isClient) {
            val manager = instance().getNetworkNodeManager(world as ServerWorld)
            val node = manager.getNode(pos)
            if (node != null) {
                removedNode = node as N
            }
            manager.removeNode(pos)
            manager.markForSaving()
//            if (node?.network?.nodeGraph != null) {
//                node.network!!.nodeGraph.invalidate(Action.PERFORM, node.network!!.world, node.network!!.position)
//            }
        }
    }

    // TODO Not sure the replacement for validate
//    fun validate() {
//        super.validate()
//        if (!world!!.isClient) {
//            val manager = instance().getNetworkNodeManager(world as ServerWorld?)
//            if (manager.getNode(pos) == null) {
//                manager.setNode(pos, createNode(world, pos))
//                manager.markForSaving()
//            }
//        }
//    }

    abstract fun createNode(world: World, pos: BlockPos): N

    // TODO Replace capabilities
//    open fun <T> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
//        return if (cap === NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
//            networkNodeProxy.cast()
//        } else super.getCapability(cap, direction)
//    }
}