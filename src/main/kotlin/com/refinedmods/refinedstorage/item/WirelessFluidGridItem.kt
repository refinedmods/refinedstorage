package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.item.INetworkItem
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.apiimpl.network.item.WirelessFluidGridNetworkItem
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import java.util.function.Supplier

class WirelessFluidGridItem(val type: Type) : NetworkItem(Properties().group(RS.MAIN_GROUP).maxStackSize(1), type == Type.CREATIVE, Supplier { RS.SERVER_CONFIG.wirelessFluidGrid.getCapacity() }) {
    enum class Type {
        NORMAL, CREATIVE
    }

    @Nonnull
    override fun provide(handler: INetworkItemManager?, player: PlayerEntity?, stack: ItemStack?, slotId: Int): INetworkItem? {
        return WirelessFluidGridNetworkItem(handler!!, player!!, stack!!, slotId)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun getSortingType(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_SORTING_TYPE)) stack.tag!!.getInt(GridNetworkNode.NBT_SORTING_TYPE) else IGrid.SORTING_TYPE_QUANTITY
        }

        @kotlin.jvm.JvmStatic
        fun getSortingDirection(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_SORTING_DIRECTION)) stack.tag!!.getInt(GridNetworkNode.NBT_SORTING_DIRECTION) else IGrid.SORTING_DIRECTION_DESCENDING
        }

        @kotlin.jvm.JvmStatic
        fun getSearchBoxMode(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_SEARCH_BOX_MODE)) stack.tag!!.getInt(GridNetworkNode.NBT_SEARCH_BOX_MODE) else IGrid.SEARCH_BOX_MODE_NORMAL
        }

        @kotlin.jvm.JvmStatic
        fun getTabSelected(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_TAB_SELECTED)) stack.tag!!.getInt(GridNetworkNode.NBT_TAB_SELECTED) else -1
        }

        @kotlin.jvm.JvmStatic
        fun getTabPage(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_TAB_PAGE)) stack.tag!!.getInt(GridNetworkNode.NBT_TAB_PAGE) else 0
        }

        @kotlin.jvm.JvmStatic
        fun getSize(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(GridNetworkNode.NBT_SIZE)) stack.tag!!.getInt(GridNetworkNode.NBT_SIZE) else IGrid.SIZE_STRETCH
        }
    }

    init {
        this.setRegistryName(RS.ID, (if (type == Type.CREATIVE) "creative_" else "") + "wireless_fluid_grid")
    }
}