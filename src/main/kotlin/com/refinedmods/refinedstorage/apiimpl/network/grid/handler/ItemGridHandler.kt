package com.refinedmods.refinedstorage.apiimpl.network.grid.handler

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartResponseMessage
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*


class ItemGridHandler(private val network: INetwork) : IItemGridHandler {
    override fun onExtract(player: ServerPlayerEntity?, id: UUID?, flags: Int) {
        val item = network.itemStorageCache!!.getList()!![id!!]
        if (item == null || !network.securityManager!!.hasPermission(Permission.EXTRACT, player)) {
            return
        }
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

        // Do this before actually extracting, since external storage sends updates as soon as a change happens (so before the storage tracker used to track)
        network.itemStorageTracker!!.changed(player, item.copy())
        var took = network.extractItem(item, size, Action.SIMULATE)
        if (!took!!.isEmpty) {
            if (flags and IItemGridHandler.EXTRACT_SHIFT == IItemGridHandler.EXTRACT_SHIFT) {
                val playerInventory: IItemHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null)
                if (playerInventory != null && ItemHandlerHelper.insertItem(playerInventory, took, true).isEmpty()) {
                    took = network.extractItem(item, size, Action.PERFORM)
                    ItemHandlerHelper.insertItem(playerInventory, took, false)
                }
            } else {
                took = network.extractItem(item, size, Action.PERFORM)
                if (!took!!.isEmpty) {
                    if (single && !held.isEmpty) {
                        held.grow(1)
                    } else {
                        player.inventory.setItemStack(took)
                    }
                    player.updateHeldItem()
                }
            }
            network.networkItemManager!!.drainEnergy(player, RS.SERVER_CONFIG.wirelessGrid.extractUsage)
        }
    }

    @Nonnull
    override fun onInsert(player: ServerPlayerEntity?, stack: ItemStack?): ItemStack? {
        if (!network.securityManager!!.hasPermission(Permission.INSERT, player)) {
            return stack
        }
        network.itemStorageTracker!!.changed(player, stack!!.copy())
        val remainder = network.insertItem(stack, stack.count, Action.PERFORM)
        network.networkItemManager!!.drainEnergy(player, RS.SERVER_CONFIG.wirelessGrid.insertUsage)
        return remainder
    }

    override fun onInsertHeldItem(player: ServerPlayerEntity?, single: Boolean) {
        if (player.inventory.getItemStack().isEmpty() || !network.securityManager!!.hasPermission(Permission.INSERT, player)) {
            return
        }
        val stack: ItemStack = player.inventory.getItemStack()
        val size = if (single) 1 else stack.count
        network.itemStorageTracker!!.changed(player, stack.copy())
        if (single) {
            if (network.insertItem(stack, size, Action.SIMULATE)!!.isEmpty) {
                network.insertItem(stack, size, Action.PERFORM)
                stack.shrink(size)
            }
        } else {
            player.inventory.setItemStack(network.insertItem(stack, size, Action.PERFORM))
        }
        player.updateHeldItem()
        network.networkItemManager!!.drainEnergy(player, RS.SERVER_CONFIG.wirelessGrid.insertUsage)
    }

    override fun onCraftingPreviewRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int, noPreview: Boolean) {
        if (!network.securityManager!!.hasPermission(Permission.AUTOCRAFTING, player)) {
            return
        }
        val stack = network.itemStorageCache!!.getCraftablesList()!![id!!]
        if (stack != null) {
            val calculationThread = Thread({
                val result: ICalculationResult = network.craftingManager.create(stack, quantity)
                if (!result.isOk() && result.getType() !== CalculationResultType.MISSING) {
                    RS.NETWORK_HANDLER.sendTo(
                            player,
                            GridCraftingPreviewResponseMessage(listOf(ErrorCraftingPreviewElement(result.getType(), if (result.getRecursedPattern() == null) ItemStack.EMPTY else result.getRecursedPattern()!!.getStack())),
                                    id,
                                    quantity,
                                    false
                            )
                    )
                } else if (result.isOk() && noPreview) {
                    network.craftingManager.start(result.getTask())
                    RS.NETWORK_HANDLER.sendTo(player, GridCraftingStartResponseMessage())
                } else {
                    RS.NETWORK_HANDLER.sendTo(
                            player,
                            GridCraftingPreviewResponseMessage(
                                    result.getPreviewElements(),
                                    id,
                                    quantity,
                                    false
                            )
                    )
                }
            }, "RS crafting preview calculation")
            calculationThread.start()
        }
    }

    override fun onCraftingRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int) {
        if (quantity <= 0 || !network.securityManager!!.hasPermission(Permission.AUTOCRAFTING, player)) {
            return
        }
        val stack = network.itemStorageCache!!.getCraftablesList()!![id!!]
        if (stack != null) {
            val result: ICalculationResult = network.craftingManager.create(stack, quantity)
            if (result.isOk()) {
                network.craftingManager.start(result.getTask())
            }
        }
    }

    override fun onCraftingCancelRequested(player: ServerPlayerEntity?, @Nullable id: UUID?) {
        if (!network.securityManager!!.hasPermission(Permission.AUTOCRAFTING, player)) {
            return
        }
        network.craftingManager.cancel(id)
        network.networkItemManager!!.drainEnergy(player, if (id == null) RS.SERVER_CONFIG.wirelessCraftingMonitor.cancelAllUsage else RS.SERVER_CONFIG.wirelessCraftingMonitor.cancelUsage)
    }
}