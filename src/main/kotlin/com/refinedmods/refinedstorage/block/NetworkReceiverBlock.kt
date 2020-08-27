package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock

@RegisterBlock(RS.ID, NetworkReceiverBlock.ID)
class NetworkReceiverBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true)
//        BlockEntityProvider
{
    companion object {
        const val ID = "network_receiver"
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
//    // TODO BlockEntities
////            = NetworkReceiverTile()

}