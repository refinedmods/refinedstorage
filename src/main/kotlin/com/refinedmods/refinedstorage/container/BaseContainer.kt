package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.container.transfer.TransferManager
import com.refinedmods.refinedstorage.network.FluidFilterSlotUpdateMessage
import com.refinedmods.refinedstorage.tile.BaseTile
import com.refinedmods.refinedstorage.tile.data.TileDataWatcher
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance
import java.util.*

abstract class BaseContainer(@Nullable type: ContainerType<*>?, @field:Nullable @get:Nullable
@param:Nullable val tile: BaseTile?, player: PlayerEntity, windowId: Int) : Container(type, windowId) {

    @Nullable
    private var listener: TileDataWatcher? = null
    val player: PlayerEntity
    protected val transferManager = TransferManager(this)
    private val fluidSlots: MutableList<FluidFilterSlot> = ArrayList()
    private val fluids: MutableList<FluidInstance> = ArrayList<FluidInstance>()
    protected fun addPlayerInventory(xInventory: Int, yInventory: Int) {
        val disabledSlotNumber = disabledSlotNumber
        var id = 9
        for (y in 0..2) {
            for (x in 0..8) {
                if (id == disabledSlotNumber) {
                    addSlot(LegacyDisabledSlot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                } else {
                    addSlot(Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                }
                id++
            }
        }
        id = 0
        for (i in 0..8) {
            val x = xInventory + i * 18
            val y = yInventory + 4 + 3 * 18
            if (id == disabledSlotNumber) {
                addSlot(LegacyDisabledSlot(player.inventory, id, x, y))
            } else {
                addSlot(Slot(player.inventory, id, x, y))
            }
            id++
        }
    }

    fun getFluidSlots(): List<FluidFilterSlot> {
        return fluidSlots
    }

    fun slotClick(id: Int, dragType: Int, clickType: ClickType, player: PlayerEntity): ItemStack {
        val slot: Slot? = if (id >= 0) getSlot(id) else null
        val disabledSlotNumber = disabledSlotNumber

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 && clickType === ClickType.SWAP && dragType == disabledSlotNumber) {
            return ItemStack.EMPTY
        }
        if (slot is FilterSlot) {
            if ((slot as FilterSlot?)!!.isSizeAllowed) {
                if (clickType === ClickType.QUICK_MOVE) {
                    slot.putStack(ItemStack.EMPTY)
                } else if (!player.inventory.getItemStack().isEmpty()) {
                    slot.putStack(player.inventory.getItemStack().copy())
                }
            } else if (player.inventory.getItemStack().isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy())
            }
            return player.inventory.getItemStack()
        } else if (slot is FluidFilterSlot) {
            if ((slot as FluidFilterSlot?)!!.isSizeAllowed) {
                if (clickType === ClickType.QUICK_MOVE) {
                    (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
                } else if (!player.inventory.getItemStack().isEmpty()) {
                    (slot as FluidFilterSlot?)!!.onContainerClicked(player.inventory.getItemStack())
                }
            } else if (player.inventory.getItemStack().isEmpty()) {
                (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
            } else {
                (slot as FluidFilterSlot?)!!.onContainerClicked(player.inventory.getItemStack())
            }
            return player.inventory.getItemStack()
        } else if (slot is LegacyFilterSlot) {
            if (player.inventory.getItemStack().isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy())
            }
            return player.inventory.getItemStack()
        } else if (slot is LegacyDisabledSlot) {
            return ItemStack.EMPTY
        }
        return super.slotClick(id, dragType, clickType, player)
    }

    open fun transferStackInSlot(player: PlayerEntity?, slotIndex: Int): ItemStack? {
        return transferManager.transfer(slotIndex)
    }

    fun canInteractWith(player: PlayerEntity?): Boolean {
        return isTileStillThere
    }

    // @Volatile: Logic from LockableLootBlockEntity#isUsableByPlayer
    private val isTileStillThere: Boolean
        private get() = if (tile != null) {
            // @Volatile: Logic from LockableLootBlockEntity#isUsableByPlayer
            tile.world!!.getBlockEntity(tile.pos) === tile
        } else true

    open fun canMergeSlot(stack: ItemStack?, slot: Slot): Boolean {
        return if (slot is FilterSlot || slot is FluidFilterSlot || slot is LegacyFilterSlot) {
            false
        } else super.canMergeSlot(stack, slot)
    }

    protected open val disabledSlotNumber: Int
        protected get() = -1

    protected fun addSlot(slot: Slot): Slot {
        if (slot is FluidFilterSlot) {
            fluids.add(FluidInstance.EMPTY)
            fluidSlots.add(slot as FluidFilterSlot)
        }
        return super.addSlot(slot)
    }

    open fun detectAndSendChanges() {
        super.detectAndSendChanges()

        // Prevent sending changes about a tile that doesn't exist anymore.
        // This prevents crashes when sending network node data (network node would crash because it no longer exists and we're querying it from the various tile data parameters).
        if (listener != null && isTileStillThere) {
            listener.detectAndSendChanges()
        }
        if (player is ServerPlayerEntity) {
            for (i in fluidSlots.indices) {
                val slot = fluidSlots[i]
                val cached: FluidInstance = fluids[i]
                val actual: FluidInstance = slot.fluidInventory.getFluid(slot.getSlotIndex())
                if (!instance().getComparer().isEqual(cached, actual, IComparer.COMPARE_QUANTITY or IComparer.COMPARE_NBT)) {
                    fluids[i] = actual.copy()
                    RS.NETWORK_HANDLER.sendTo(player as ServerPlayerEntity, FluidFilterSlotUpdateMessage(slot.slotNumber, actual))
                }
            }
        }
    }

    open fun onContainerClosed(player: PlayerEntity) {
        super.onContainerClosed(player)
        if (listener != null) {
            listener.onClosed()
        }
    }

    init {
        if (tile != null && player is ServerPlayerEntity) {
            listener = TileDataWatcher(player as ServerPlayerEntity, tile.getDataManager())
        }
        this.player = player
    }
}