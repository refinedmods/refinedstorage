package com.refinedmods.refinedstorage.tile.grid

import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSearchBoxMode
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSize
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingDirection
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingType
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidViewType
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import com.refinedmods.refinedstorage.util.GridUtils
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.Direction
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

class GridTile(private val type: GridType) : NetworkNodeTile<GridNetworkNode?>(GridUtils.getBlockEntityType(type)) {
    private val diskCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.getPatterns() })
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): GridNetworkNode {
        return GridNetworkNode(world, pos, type)
    }

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && type === GridType.PATTERN) {
            diskCapability.cast()
        } else super.getCapability<T>(cap, direction)
    }

    companion object {
        val VIEW_TYPE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getViewType() }, BiConsumer<GridTile, Int> { t: GridTile, v: Int? ->
            if (isValidViewType(v!!)) {
                t.getNode()!!.setViewType(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> trySortGrid(initial) })
        val SORTING_DIRECTION = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getSortingDirection() }, BiConsumer<GridTile, Int> { t: GridTile, v: Int? ->
            if (isValidSortingDirection(v!!)) {
                t.getNode()!!.setSortingDirection(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> trySortGrid(initial) })
        val SORTING_TYPE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getSortingType() }, BiConsumer<GridTile, Int> { t: GridTile, v: Int? ->
            if (isValidSortingType(v!!)) {
                t.getNode()!!.setSortingType(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> trySortGrid(initial) })
        val SEARCH_BOX_MODE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getSearchBoxMode() }, BiConsumer<GridTile, Int> { t: GridTile, v: Int? ->
            if (isValidSearchBoxMode(v!!)) {
                t.getNode()!!.setSearchBoxMode(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getSearchField().setMode(p) }) })
        val SIZE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getSize() }, BiConsumer<GridTile, Int> { t: GridTile, v: Int? ->
            if (isValidSize(v!!)) {
                t.getNode()!!.setSize(v)
                t.getNode()!!.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { screen -> screen.resize(screen.getMinecraft(), screen.width, screen.height) }) })
        val TAB_SELECTED = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getTabSelected() }, BiConsumer { t: GridTile, v: Int ->
            t.getNode()!!.setTabSelected(if (v == t.getNode()!!.getTabSelected()) -1 else v)
            t.getNode()!!.markDirty()
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() }) })
        val TAB_PAGE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: GridTile -> t.getNode()!!.getTabPage() }, BiConsumer { t: GridTile, v: Int ->
            if (v >= 0 && v <= t.getNode()!!.totalTabPages) {
                t.getNode()!!.setTabPage(v)
                t.getNode()!!.markDirty()
            }
        })
        val EXACT_PATTERN = TileDataParameter(DataSerializers.BOOLEAN, true, Function { t: GridTile -> t.getNode()!!.isExactPattern }, BiConsumer<GridTile, Boolean> { t: GridTile, v: Boolean? ->
            t.getNode()!!.isExactPattern = v!!
            t.getNode()!!.markDirty()
        }, TileDataParameterClientListener<Boolean> { initial: Boolean, p: Boolean? -> BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.updateExactPattern(p) }) })
        val PROCESSING_PATTERN = TileDataParameter(DataSerializers.BOOLEAN, false, Function { t: GridTile -> t.getNode()!!.isProcessingPattern() }, BiConsumer<GridTile, Boolean> { t: GridTile, v: Boolean? ->
            t.getNode()!!.setProcessingPattern(v!!)
            t.getNode()!!.clearMatrix()
            t.getNode()!!.markDirty()
        }, TileDataParameterClientListener<Boolean> { initial: Boolean, p: Boolean? -> BaseScreen.executeLater(GridScreen::class.java, { obj: BaseScreen<*> -> obj.init() }) })
        val PROCESSING_TYPE: TileDataParameter<Int, GridTile> = IType.Companion.createParameter(TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { obj: BaseScreen<*> -> obj.init() }) })
        val ALLOWED_ITEM_TAGS: TileDataParameter<List<Set<Identifier>>, GridTile> = TileDataParameter<T, E>(RSSerializers.LIST_OF_SET_SERIALIZER, ArrayList<Any>(), Function<E, T> { t: E -> t.getNode().getAllowedTagList().getAllowedItemTags() }, BiConsumer<E, T> { t: E, v: T? -> t.getNode().getAllowedTagList().setAllowedItemTags(v) })
        val ALLOWED_FLUID_TAGS: TileDataParameter<List<Set<Identifier>>, GridTile> = TileDataParameter<T, E>(RSSerializers.LIST_OF_SET_SERIALIZER, ArrayList<Any>(), Function<E, T> { t: E -> t.getNode().getAllowedTagList().getAllowedFluidTags() }, BiConsumer<E, T> { t: E, v: T? -> t.getNode().getAllowedTagList().setAllowedFluidTags(v) })
        fun trySortGrid(initial: Boolean) {
            if (!initial) {
                BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() })
            }
        }
    }

    init {
        dataManager.addWatchedParameter(VIEW_TYPE)
        dataManager.addWatchedParameter(SORTING_DIRECTION)
        dataManager.addWatchedParameter(SORTING_TYPE)
        dataManager.addWatchedParameter(SEARCH_BOX_MODE)
        dataManager.addWatchedParameter(SIZE)
        dataManager.addWatchedParameter(TAB_SELECTED)
        dataManager.addWatchedParameter(TAB_PAGE)
        dataManager.addWatchedParameter(EXACT_PATTERN)
        dataManager.addWatchedParameter(PROCESSING_PATTERN)
        dataManager.addWatchedParameter(PROCESSING_TYPE)
        dataManager.addParameter(ALLOWED_ITEM_TAGS)
        dataManager.addParameter(ALLOWED_FLUID_TAGS)
    }
}