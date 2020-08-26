package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.item.INetworkItem
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.apiimpl.network.item.WirelessCraftingMonitorNetworkItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import java.util.*
import java.util.function.Supplier

class WirelessCraftingMonitorItem(type: Type) : NetworkItem(Properties().group(RS.MAIN_GROUP).maxStackSize(1), type == Type.CREATIVE, Supplier { RS.SERVER_CONFIG.wirelessCraftingMonitor.getCapacity() }) {
    enum class Type {
        NORMAL, CREATIVE
    }

    val type: Type
    @Nonnull
    override fun provide(handler: INetworkItemManager?, player: PlayerEntity?, stack: ItemStack?, slotId: Int): INetworkItem? {
        return WirelessCraftingMonitorNetworkItem(handler!!, player!!, stack!!, slotId)
    }

    companion object {
        const val NBT_TAB_SELECTED = "TabSelected"
        const val NBT_TAB_PAGE = "TabPage"
        @kotlin.jvm.JvmStatic
        fun getTabSelected(stack: ItemStack): Optional<UUID> {
            return if (stack.hasTag() && stack.tag.hasUniqueId(NBT_TAB_SELECTED)) {
                Optional.of(stack.tag.getUniqueId(NBT_TAB_SELECTED))
            } else Optional.empty()
        }

        @kotlin.jvm.JvmStatic
        fun setTabSelected(stack: ItemStack, tabSelected: Optional<UUID?>) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            if (tabSelected.isPresent) {
                stack.tag.putUniqueId(NBT_TAB_SELECTED, tabSelected.get())
            } else {
                stack.tag!!.remove(NBT_TAB_SELECTED + "Least")
                stack.tag!!.remove(NBT_TAB_SELECTED + "Most")
            }
        }

        @kotlin.jvm.JvmStatic
        fun getTabPage(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_TAB_PAGE)) {
                stack.tag!!.getInt(NBT_TAB_PAGE)
            } else 0
        }

        @kotlin.jvm.JvmStatic
        fun setTabPage(stack: ItemStack, tabPage: Int) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_TAB_PAGE, tabPage)
        }
    }

    init {
        this.setRegistryName(RS.ID, (if (type == Type.CREATIVE) "creative_" else "") + "wireless_crafting_monitor")
        this.type = type
    }
}