package com.refinedmods.refinedstorage.apiimpl.network.grid.handler

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartResponseMessage
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.refinedmods.refinedstorage.util.StackUtils.getFluid
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import org.apache.commons.lang3.tuple.Pair
import java.util.*


class FluidGridHandler(private val network: INetwork) : IFluidGridHandler {
    override fun onExtract(player: ServerPlayerEntity?, id: UUID?, shift: Boolean) {
        val stack: FluidInstance? = network.fluidStorageCache!!.getList()!![id]
        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME || !network.securityManager!!.hasPermission(Permission.EXTRACT, player)) {
            return
        }
        NetworkUtils.extractBucketFromPlayerInventoryOrNetwork(player, network) { bucket: ItemStack ->
            bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent({ fluidHandler ->
                network.fluidStorageTracker!!.changed(player, stack.copy())
                val extracted: FluidInstance? = network.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, Action.PERFORM)
                fluidHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE)
                if (shift) {
                    if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), fluidHandler.getContainer())
                    }
                } else {
                    player.inventory.setItemStack(fluidHandler.getContainer())
                    player.updateHeldItem()
                }
                network.networkItemManager!!.drainEnergy(player, RS.SERVER_CONFIG.wirelessFluidGrid.extractUsage)
            })
        }
    }

    @Nonnull
    override fun onInsert(player: ServerPlayerEntity?, container: ItemStack?): ItemStack? {
        if (!network.securityManager!!.hasPermission(Permission.INSERT, player)) {
            return container
        }
        var result: Pair<ItemStack?, FluidInstance> = getFluid(container!!, true)
        if (!result.value.isEmpty() && network.insertFluid(result.value, result.value.getAmount(), Action.SIMULATE).isEmpty()) {
            network.fluidStorageTracker!!.changed(player, result.value.copy())
            result = getFluid(container, false)
            network.insertFluid(result.value, result.value.getAmount(), Action.PERFORM)
            network.networkItemManager!!.drainEnergy(player, RS.SERVER_CONFIG.wirelessFluidGrid.insertUsage)
            return result.left
        }
        return container
    }

    override fun onInsertHeldContainer(player: ServerPlayerEntity?) {
        player.inventory.setItemStack(onInsert(player, player.inventory.getItemStack()))
        player.updateHeldItem()
    }

    override fun onCraftingPreviewRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int, noPreview: Boolean) {
        if (!network.securityManager!!.hasPermission(Permission.AUTOCRAFTING, player)) {
            return
        }
        val stack: FluidInstance? = network.fluidStorageCache!!.getCraftablesList()!![id]
        if (stack != null) {
            val calculationThread = Thread(label@ Runnable {
                val result: ICalculationResult = network.craftingManager.create(stack, quantity) ?: return@label
                if (!result.isOk() && result.getType() !== CalculationResultType.MISSING) {
                    RS.NETWORK_HANDLER.sendTo(
                            player,
                            GridCraftingPreviewResponseMessage(listOf(ErrorCraftingPreviewElement(result.getType(), if (result.getRecursedPattern() == null) ItemStack.EMPTY else result.getRecursedPattern()!!.getStack())),
                                    id,
                                    quantity,
                                    true
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
                                    true
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
        val stack: FluidInstance? = network.fluidStorageCache!!.getCraftablesList()!![id]
        if (stack != null) {
            val result: ICalculationResult = network.craftingManager.create(stack, quantity)
            if (result.isOk()) {
                network.craftingManager.start(result.getTask())
            }
        }
    }
}