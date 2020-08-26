package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.RS
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CableNetworkNode(
        world: World,
        pos: BlockPos,
        override val id: Identifier = ID
): NetworkNode(world, pos) {
    companion object {
        val ID: Identifier = Identifier(RS.ID, "cable")
    }

    override val energyUsage: Int
        get() = RS.SERVER_CONFIG.cable.getUsage()

}