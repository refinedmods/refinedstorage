package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot
import com.refinedmods.refinedstorage.container.slot.grid.ResultCraftingGridSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyBaseSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider
import com.refinedmods.refinedstorage.tile.BaseTile
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.IContainerListener
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SSetSlotPacket
import net.minecraftforge.items.SlotItemHandler

class GridContainer(val grid: IGrid?, @Nullable gridTile: BaseTile?, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.GRID, gridTile, player, windowId), ICraftingGridListener {
    private var cache: IStorageCache<*>? = null
    private var listener: IStorageCacheListener<*>? = null
    private var screenInfoProvider: IScreenInfoProvider? = null
    private var craftingResultSlot: ResultCraftingGridSlot? = null
    private var patternResultSlot: LegacyBaseSlot? = null
    fun setScreenInfoProvider(screenInfoProvider: IScreenInfoProvider?) {
        this.screenInfoProvider = screenInfoProvider
    }

    fun initSlots() {
        this.inventorySlots.clear()
        this.inventoryItemStacks.clear()
        transferManager.clearTransfers()
        addFilterSlots()
        if (grid is IPortableGrid) {
            addPortableGridSlots()
        }
        if (grid!!.gridType === GridType.CRAFTING) {
            addCraftingSlots()
        } else if (grid!!.gridType === GridType.PATTERN) {
            addPatternSlots()
        }
        transferManager.setNotFoundHandler { slotIndex: Int? ->
            if (!player.entityWorld.isRemote) {
                val slot: Slot = inventorySlots.get(slotIndex)
                if (grid is IPortableGrid && slot is SlotItemHandler) {
                    if ((slot as SlotItemHandler).getItemHandler().equals((grid as IPortableGrid).disk)) {
                        return@setNotFoundHandler ItemStack.EMPTY
                    }
                }
                if (slot.getHasStack()) {
                    if (slot === craftingResultSlot) {
                        grid!!.onCraftedShift(player)
                        detectAndSendChanges()
                    } else {
                        val stack: ItemStack = slot.getStack()
                        if (grid!!.gridType === GridType.FLUID) {
                            val fluidHandler = grid!!.fluidHandler
                            if (fluidHandler != null) {
                                slot.putStack(fluidHandler.onInsert(player as ServerPlayerEntity, stack))
                            }
                        } else {
                            val itemHandler = grid!!.itemHandler
                            if (itemHandler != null) {
                                slot.putStack(itemHandler.onInsert(player as ServerPlayerEntity, stack))
                            } else if (slot is CraftingGridSlot && mergeItemStack(stack, 14, 14 + 9 * 4, false)) {
                                slot.onSlotChanged()

                                // This is needed because when a grid is disconnected,
                                // and a player shift clicks from the matrix to the inventory (this if case),
                                // the crafting inventory isn't being notified.
                                grid.onCraftingMatrixChanged()
                            }
                        }
                        detectAndSendChanges()
                    }
                }
            }
            ItemStack.EMPTY
        }
        addPlayerInventory(8, screenInfoProvider!!.yPlayerInventory)
    }

    private fun addPortableGridSlots() {
        addSlot(SlotItemHandler((grid as IPortableGrid?)!!.disk, 0, 204, 6))
        transferManager.addBiTransfer(player.inventory, (grid as IPortableGrid?)!!.disk)
    }

    private fun addFilterSlots() {
        var yStart = 6
        if (grid is IPortableGrid) {
            yStart = 38
        }
        for (i in 0..3) {
            addSlot(SlotItemHandler(grid!!.filter, i, 204, yStart + 18 * i))
        }
        transferManager.addBiTransfer(player.inventory, grid!!.filter)
    }

    private fun addCraftingSlots() {
        val headerAndSlots = screenInfoProvider!!.topHeight + screenInfoProvider!!.visibleRows * 18
        var x = 26
        var y = headerAndSlots + 4
        for (i in 0..8) {
            addSlot(CraftingGridSlot(grid!!.craftingMatrix, i, x, y))
            x += 18
            if ((i + 1) % 3 == 0) {
                y += 18
                x = 26
            }
        }
        addSlot(ResultCraftingGridSlot(player, grid, 0, 130 + 4, headerAndSlots + 22).also { craftingResultSlot = it })
    }

    private fun addPatternSlots() {
        val headerAndSlots = screenInfoProvider!!.topHeight + screenInfoProvider!!.visibleRows * 18
        addSlot(SlotItemHandler((grid as GridNetworkNode?)!!.getPatterns(), 0, 172, headerAndSlots + 4))
        addSlot(SlotItemHandler((grid as GridNetworkNode?)!!.getPatterns(), 1, 172, headerAndSlots + 40))
        transferManager.addBiTransfer(player.inventory, (grid as GridNetworkNode?)!!.getPatterns())

        // Processing patterns
        var ox = 8
        var x = ox
        var y = headerAndSlots + 4
        for (i in 0 until 9 * 2) {
            var itemFilterSlotConfig: Int = FilterSlot.Companion.FILTER_ALLOW_SIZE
            if (i < 9) {
                itemFilterSlotConfig = itemFilterSlotConfig or FilterSlot.Companion.FILTER_ALLOW_ALTERNATIVES
            }
            var fluidFilterSlotConfig: Int = FluidFilterSlot.Companion.FILTER_ALLOW_SIZE
            if (i < 9) {
                fluidFilterSlotConfig = fluidFilterSlotConfig or FluidFilterSlot.Companion.FILTER_ALLOW_ALTERNATIVES
            }
            addSlot(FilterSlot((grid as GridNetworkNode?)!!.getProcessingMatrix(), i, x, y, itemFilterSlotConfig).setEnableHandler { (grid as GridNetworkNode?)!!.isProcessingPattern() && (grid as GridNetworkNode?)!!.getType() == IType.ITEMS })
            addSlot(FluidFilterSlot((grid as GridNetworkNode?)!!.getProcessingMatrixFluids(), i, x, y, fluidFilterSlotConfig).setEnableHandler { (grid as GridNetworkNode?)!!.isProcessingPattern() && (grid as GridNetworkNode?)!!.getType() == IType.FLUIDS })
            x += 18
            if ((i + 1) % 3 == 0) {
                if (i == 8) {
                    ox = 98
                    x = ox
                    y = headerAndSlots + 4
                } else {
                    x = ox
                    y += 18
                }
            }
        }

        // Regular patterns
        x = 26
        y = headerAndSlots + 4
        for (i in 0..8) {
            addSlot(LegacyFilterSlot(grid!!.craftingMatrix, i, x, y).setEnableHandler { !(grid as GridNetworkNode?)!!.isProcessingPattern() })
            x += 18
            if ((i + 1) % 3 == 0) {
                y += 18
                x = 26
            }
        }
        addSlot(LegacyDisabledSlot(grid!!.craftingResult, 0, 134, headerAndSlots + 22).setEnableHandler { !(grid as GridNetworkNode?)!!.isProcessingPattern() }.also { patternResultSlot = it })
    }

    override fun onCraftingMatrixChanged() {
        for (i in 0 until inventorySlots.size()) {
            val slot: Slot = inventorySlots.get(i)
            if (slot is CraftingGridSlot || slot === craftingResultSlot || slot === patternResultSlot) {
                for (listener in listeners) {
                    // @Volatile: We can't use IContainerListener#sendSlotContents since ServerPlayerEntity blocks CraftingResultSlot changes...
                    if (listener is ServerPlayerEntity) {
                        (listener as ServerPlayerEntity).connection.sendPacket(SSetSlotPacket(windowId, i, slot.getStack()))
                    }
                }
            }
        }
    }

    override fun detectAndSendChanges() {
        if (!player.world.isClient) {
            // The grid is offline.
            if (grid!!.storageCache == null) {
                // The grid just went offline, there is still a listener.
                if (listener != null) {
                    // Remove it from the previous cache and clean up.
                    cache!!.removeListener(listener)
                    listener = null
                    cache = null
                }
            } else if (listener == null) { // The grid came online.
                listener = grid.createListener(player as ServerPlayerEntity)
                cache = grid.storageCache
                cache!!.addListener(listener)
            }
        }
        super.detectAndSendChanges()
    }

    override fun onContainerClosed(player: PlayerEntity) {
        super.onContainerClosed(player)
        if (!player.entityWorld.isRemote) {
            grid!!.onClosed(player)
            if (cache != null && listener != null) {
                cache!!.removeListener(listener)
            }
        }
        grid!!.removeCraftingListener(this)
    }

    override fun canMergeSlot(stack: ItemStack?, slot: Slot): Boolean {
        return if (slot === craftingResultSlot || slot === patternResultSlot) {
            false
        } else super.canMergeSlot(stack, slot)
    }

    init {
        grid!!.addCraftingListener(this)
    }
}