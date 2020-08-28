package com.refinedmods.refinedstorage.block

//import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
//import com.refinedmods.refinedstorage.apiimpl.API
//import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.api.component.INetworkNodeProxyComponent
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.extensions.getCustomLogger
//import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

abstract class NetworkNodeBlock(
        settings: Settings
):
        BaseBlock(settings)
{

    // TODO Network
    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
        if (!world.isClient) {
            val node = API.instance().getNetworkNodeManager(world as ServerWorld).getNode(pos)
            if (node is NetworkNode) {
                node.setRedstonePowered(world.isReceivingRedstonePower(pos))
            }
        }
    }

//    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
//        if (state.block !== newState.block) {
//            // Different block, drop inventory
//            world.getBlockEntity(pos)?.let { entity ->
//                if (entity is NetworkNodeTile<*>) {
//                    entity.node.drops?.let { inventory ->
//                        inventory.drop(world, pos)
//                    }
//                }
//            }
//
//        }
//
//        // Call onReplaced after the drops check so the tile still exists
//        super.onStateReplaced(state, world, pos, newState, moved)
//    }

    override fun onDirectionChanged(world: World, pos: BlockPos, newDirection: Direction) {
        super.onDirectionChanged(world, pos, newDirection)

        world.getBlockEntity(pos)?.let {
            if (it is INetworkNodeProxy<*>) {
                val node = (it as INetworkNodeProxy<*>).node
                if (node is NetworkNode) {
                    node.onDirectionChanged(newDirection)
                }
            }
        }
    }

    companion object {
        @JvmField
        val CONNECTED: BooleanProperty = BooleanProperty.of("connected")
    }

}