package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack

class CraftingMonitorContainer(type: ContainerType<CraftingMonitorContainer?>?, val craftingMonitor: ICraftingMonitor, @Nullable craftingMonitorTile: CraftingMonitorTile?, player: PlayerEntity, windowId: Int) : BaseContainer(type, craftingMonitorTile, player, windowId), ICraftingMonitorListener {
    private var addedListener = false
    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        if (!player.world.isClient) {
            val manager = craftingMonitor.craftingManager
            if (manager != null && !addedListener) {
                manager.addListener(this)
                addedListener = true
            } else if (manager == null && addedListener) {
                addedListener = false
            }
        }
    }

    override fun onContainerClosed(player: PlayerEntity) {
        super.onContainerClosed(player)
        if (!player.entityWorld.isRemote) {
            craftingMonitor.onClosed(player)
            val manager = craftingMonitor.craftingManager
            if (manager != null && addedListener) {
                manager.removeListener(this)
            }
        }
    }

    override fun transferStackInSlot(player: PlayerEntity?, index: Int): ItemStack? {
        var stack = ItemStack.EMPTY
        val slot: Slot = getSlot(index)
        if (slot.getHasStack()) {
            stack = slot.getStack()
            if (index < 4) {
                if (!mergeItemStack(stack, 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY
                }
            } else if (!mergeItemStack(stack, 0, 4, false)) {
                return ItemStack.EMPTY
            }
            if (stack.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }
        return stack
    }

    protected override val disabledSlotNumber: Int
        protected get() = craftingMonitor.slotId

    override fun onAttached() {
        onChanged()
    }

    override fun onChanged() {
        RS.NETWORK_HANDLER.sendTo(player as ServerPlayerEntity, CraftingMonitorUpdateMessage(craftingMonitor))
    }
}