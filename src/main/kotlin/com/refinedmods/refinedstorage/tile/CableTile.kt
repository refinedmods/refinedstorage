package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.network.node.CableNetworkNode
import com.refinedmods.refinedstorage.block.CableBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlockEntity(RS.ID, CableBlock.ID, ["CABLE_BLOCK"])
class CableTile : NetworkNodeTile<CableNetworkNode>(BlockEntityRegistryGenerated.CABLE_TILE) {
    override fun createNode(world: World, pos: BlockPos): CableNetworkNode {
        return CableNetworkNode(world, pos)
    }
}