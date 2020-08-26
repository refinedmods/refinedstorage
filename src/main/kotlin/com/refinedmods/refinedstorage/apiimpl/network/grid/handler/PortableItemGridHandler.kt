package com.refinedmods.refinedstorage.apiimpl.network.grid.handler

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*


class PortableItemGridHandler(private val portableGrid: IPortableGrid, private val grid: IGrid) : IItemGridHandler {
    override fun onExtract(player: ServerPlayerEntity?, id: UUID?, flags: Int) {
        if (portableGrid.storage == null || !grid.isGridActive) {
            return
        }
        val item = portableGrid.itemCache.getList()!![id!!] ?: return
        val itemSize = item.count
        // We copy here because some mods change the NBT tag of an item after getting the stack limit
        val maxItemSize: Int = item.item.getItemStackLimit(item.copy())
        val single = flags and IItemGridHandler.EXTRACT_SINGLE == IItemGridHandler.EXTRACT_SINGLE
        val held: ItemStack = player.inventory.getItemStack()
        if (single) {
            if (!held.isEmpty && (!instance().getComparer()!!.isEqualNoQuantity(item, held) || held.count + 1 > held.getMaxStackSize())) {
                return
            }
        } else if (!player.inventory.getItemStack().isEmpty()) {
            return
        }
        var size = 64
        if (flags and IItemGridHandler.EXTRACT_HALF == IItemGridHandler.EXTRACT_HALF && itemSize > 1) {
            size = itemSize / 2

            // Rationale for this check:
            // If we have 32 buckets, and we want to extract half, we expect/need to get 8 (max stack size 16 / 2).
            // Without this check, we would get 16 (total stack size 32 / 2).
            // Max item size also can't be 1. Otherwise, if we want to extract half of 8 lava buckets, we would get size 0 (1 / 2).
            if (size > maxItemSize / 2 && maxItemSize != 1) {
                size = maxItemSize / 2
            }
        } else if (single) {
            size = 1
        } else if (flags and IItemGridHandler.EXTRACT_SHIFT == IItemGridHandler.EXTRACT_SHIFT) {
            // NO OP, the quantity already set (64) is needed for shift
        }
        size = Math.min(size, maxItemSize)

        // Do this before actually extracting, since portable grid sends updates as soon as a change happens (so before the storage tracker used to track)
        portableGrid.itemStorageTracker.changed(player, item.copy())
        var took = portableGrid.itemStorage.extract(item, size, IComparer.COMPARE_NBT, Action.SIMULATE)
        if (!took.isEmpty) {
            if (flags and IItemGridHandler.EXTRACT_SHIFT == IItemGridHandler.EXTRACT_SHIFT) {
                val playerInventory: IItemHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null)
                if (playerInventory != null && ItemHandlerHelper.insertItem(playerInventory, took, true).isEmpty()) {
                    took = portableGrid.itemStorage.extract(item, size, IComparer.COMPARE_NBT, Action.PERFORM)
                    ItemHandlerHelper.insertItem(playerInventory, took, false)
                }
            } else {
                took = portableGrid.itemStorage.extract(item, size, IComparer.COMPARE_NBT, Action.PERFORM)
                if (single && !held.isEmpty) {
                    held.grow(1)
                } else {
                    player.inventory.setItemStack(took)
                }
                player.updateHeldItem()
            }
            portableGrid.drainEnergy(RS.SERVER_CONFIG.portableGrid.extractUsage)
        }
    }

    @Nonnull
    override fun onInsert(player: ServerPlayerEntity?, stack: ItemStack?): ItemStack? {
        if (portableGrid.storage == null || !grid.isGridActive) {
            return stack
        }
        portableGrid.itemStorageTracker.changed(player, stack!!.copy())
        val remainder = portableGrid.itemStorage.insert(stack, stack.count, Action.PERFORM)
        portableGrid.drainEnergy(RS.SERVER_CONFIG.portableGrid.insertUsage)
        return remainder
    }

    override fun onInsertHeldItem(player: ServerPlayerEntity?, single: Boolean) {
        if (player.inventory.getItemStack().isEmpty() || portableGrid.storage == null || !grid.isGridActive) {
            return
        }
        val stack: ItemStack = player.inventory.getItemStack()
        val size = if (single) 1 else stack.count
        portableGrid.itemStorageTracker.changed(player, stack.copy())
        if (single) {
            if (portableGrid.itemStorage.insert(stack, size, Action.SIMULATE).isEmpty) {
                portableGrid.itemStorage.insert(stack, size, Action.PERFORM)
                stack.shrink(size)
            }
        } else {
            player.inventory.setItemStack(portableGrid.itemStorage.insert(stack, size, Action.PERFORM))
        }
        player.updateHeldItem()
        portableGrid.drainEnergy(RS.SERVER_CONFIG.portableGrid.insertUsage)
    }

    override fun onCraftingPreviewRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int, noPreview: Boolean) {
        // NO OP
    }

    override fun onCraftingRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int) {
        // NO OP
    }

    override fun onCraftingCancelRequested(player: ServerPlayerEntity?, @Nullable id: UUID?) {
        // NO OP
    }
}