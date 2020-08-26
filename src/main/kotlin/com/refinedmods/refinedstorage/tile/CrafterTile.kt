package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode.CrafterMode.Companion.getById
import com.refinedmods.refinedstorage.screen.CrafterTileDataParameterClientListener
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

class CrafterTile : NetworkNodeTile<CrafterNetworkNode?>(RSTiles.CRAFTER) {
    private val patternsCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.patternItems })
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): CrafterNetworkNode {
        return CrafterNetworkNode(world, pos)
    }

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (direction != null && !direction.equals(getNode()!!.direction)) {
                return patternsCapability.cast()
            }
        }
        return super.getCapability<T>(cap, direction)
    }

    companion object {
        val MODE = TileDataParameter(DataSerializers.VARINT, CrafterNetworkNode.CrafterMode.IGNORE.ordinal, Function { t: CrafterTile -> t.getNode()!!.getMode().ordinal }, BiConsumer<CrafterTile, Int> { t: CrafterTile, v: Int? -> t.getNode()!!.setMode(getById(v!!)) })
        private val HAS_ROOT = TileDataParameter(DataSerializers.BOOLEAN, false, Function { t: CrafterTile -> t.getNode()!!.rootContainerNotSelf.isPresent }, null, TileDataParameterClientListener { t: Boolean, v: Boolean -> CrafterTileDataParameterClientListener().onChanged(t, v) })
    }

    init {
        dataManager.addWatchedParameter(MODE)
        dataManager.addParameter(HAS_ROOT)
    }
}