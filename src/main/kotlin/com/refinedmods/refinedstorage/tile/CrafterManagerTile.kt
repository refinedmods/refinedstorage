package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSearchBoxMode
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSize
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.CrafterManagerScreen
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

class CrafterManagerTile : NetworkNodeTile<CrafterManagerNetworkNode>(RSTiles.CRAFTER_MANAGER) {
    override fun createNode(world: World?, pos: BlockPos?): CrafterManagerNetworkNode {
        return CrafterManagerNetworkNode(world, pos)
    }

    companion object {
        val SIZE = TileDataParameter(DataSerializers.VARINT, IGrid.SIZE_STRETCH, Function { t: CrafterManagerTile -> t.getNode()!!.getSize() }, BiConsumer<CrafterManagerTile, Int> { t: CrafterManagerTile, v: Int? ->
            if (isValidSize(v!!)) {
                t.getNode()!!.setSize(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { _: Boolean, _: Int? -> BaseScreen.executeLater(CrafterManagerScreen::class.java, { obj: BaseScreen<*> -> obj.init() }) })
        val SEARCH_BOX_MODE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: CrafterManagerTile -> t.getNode()!!.getSearchBoxMode() }, BiConsumer<CrafterManagerTile, Int> { t: CrafterManagerTile, v: Int? ->
            if (isValidSearchBoxMode(v!!)) {
                t.getNode()!!.setSearchBoxMode(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(CrafterManagerScreen::class.java, { crafterManager -> crafterManager.getSearchField().setMode(p) }) })
    }

    init {
        dataManager.addWatchedParameter(SIZE)
        dataManager.addWatchedParameter(SEARCH_BOX_MODE)
    }
}