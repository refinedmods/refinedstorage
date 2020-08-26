package com.refinedmods.refinedstorage.tile.craftingmonitor

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

class CraftingMonitorTile : NetworkNodeTile<CraftingMonitorNetworkNode?>(RSTiles.CRAFTING_MONITOR) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): CraftingMonitorNetworkNode {
        return CraftingMonitorNetworkNode(world, pos)
    }

    companion object {
        val TAB_SELECTED = TileDataParameter(DataSerializers.OPTIONAL_UNIQUE_ID, Optional.empty(), Function { t: CraftingMonitorTile -> t.getNode()!!.getTabSelected() }, BiConsumer { t: CraftingMonitorTile, v: Optional<UUID> ->
            if (v.isPresent && t.getNode()!!.getTabSelected().isPresent && v.get() == t.getNode()!!.getTabSelected().get()) {
                t.getNode()!!.setTabSelected(Optional.empty())
            } else {
                t.getNode()!!.setTabSelected(v)
            }
            t.getNode()!!.markDirty()
        })
        val TAB_PAGE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: CraftingMonitorTile -> t.getNode()!!.getTabPage() }, BiConsumer { t: CraftingMonitorTile, v: Int ->
            if (v >= 0) {
                t.getNode()!!.setTabPage(v)
                t.getNode()!!.markDirty()
            }
        })
    }

    init {
        dataManager.addWatchedParameter(TAB_SELECTED)
        dataManager.addWatchedParameter(TAB_PAGE)
    }
}