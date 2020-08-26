package com.refinedmods.refinedstorage.apiimpl.network.grid.handler

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.util.StackUtils
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


class PortableFluidGridHandler(private val portableGrid: IPortableGrid) : IFluidGridHandler {
    override fun onExtract(player: ServerPlayerEntity?, id: UUID?, shift: Boolean) {
        val stack: FluidInstance? = portableGrid.fluidCache.getList()!![id!!]
        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
            return
        }
        var bucket = ItemStack.EMPTY
        for (i in 0 until player.inventory.getSizeInventory()) {
            val slot: ItemStack = player.inventory.getStackInSlot(i)
            if (instance().getComparer()!!.isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                bucket = StackUtils.EMPTY_BUCKET.copy()
                player.inventory.decrStackSize(i, 1)
                break
            }
        }
        if (!bucket.isEmpty) {
            bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent({ fluidHandler ->
                portableGrid.fluidStorageTracker.changed(player, stack.copy())
                fluidHandler.fill(portableGrid.fluidStorage.extract(stack, FluidAttributes.BUCKET_VOLUME, IComparer.COMPARE_NBT, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE)
                if (shift) {
                    if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), fluidHandler.getContainer())
                    }
                } else {
                    player.inventory.setItemStack(fluidHandler.getContainer())
                    player.updateHeldItem()
                }
                portableGrid.drainEnergy(RS.SERVER_CONFIG.portableGrid.extractUsage)
            })
        }
    }

    @Nonnull
    override fun onInsert(player: ServerPlayerEntity?, container: ItemStack?): ItemStack? {
        var result: Pair<ItemStack?, FluidInstance> = getFluid(container!!, true)
        if (!result.value.isEmpty() && portableGrid.fluidStorage.insert(result.value, result.value.getAmount(), Action.SIMULATE).isEmpty()) {
            portableGrid.fluidStorageTracker.changed(player, result.value.copy())
            result = getFluid(container, false)
            portableGrid.fluidStorage.insert(result.value, result.value.getAmount(), Action.PERFORM)
            portableGrid.drainEnergy(RS.SERVER_CONFIG.portableGrid.insertUsage)
            return result.left
        }
        return container
    }

    override fun onInsertHeldContainer(player: ServerPlayerEntity?) {
        player.inventory.setItemStack(onInsert(player, player.inventory.getItemStack()))
        player.updateHeldItem()
    }

    override fun onCraftingPreviewRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int, noPreview: Boolean) {
        // NO OP
    }

    override fun onCraftingRequested(player: ServerPlayerEntity?, id: UUID?, quantity: Int) {
        // NO OP
    }
}