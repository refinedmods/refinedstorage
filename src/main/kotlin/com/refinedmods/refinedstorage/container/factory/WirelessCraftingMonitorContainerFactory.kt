package com.refinedmods.refinedstorage.container.factory

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.IContainerFactory

class WirelessCraftingMonitorContainerFactory : IContainerFactory<CraftingMonitorContainer?> {
    fun create(windowId: Int, inv: PlayerInventory, data: PacketByteBuf): CraftingMonitorContainer {
        val slotId: Int = data.readInt()
        val stack: ItemStack = inv.getStackInSlot(slotId)
        val wirelessCraftingMonitor = WirelessCraftingMonitor(stack, null, slotId)
        return CraftingMonitorContainer(RSContainers.WIRELESS_CRAFTING_MONITOR, wirelessCraftingMonitor, null, inv.player, windowId)
    }
}