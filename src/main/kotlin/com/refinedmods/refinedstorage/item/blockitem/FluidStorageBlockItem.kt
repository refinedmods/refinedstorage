package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.apiimpl.storageimport.FluidStorageType
import com.refinedmods.refinedstorage.block.FluidStorageBlock
import com.refinedmods.refinedstorage.item.FluidStoragePartItem
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.util.*

class FluidStorageBlockItem(block: FluidStorageBlock) : BaseBlockItem(block, Properties().group(RS.MAIN_GROUP)) {
    private val type: FluidStorageType
    fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag) {
        super.addInformation(stack, world, tooltip, flag)
        if (isValid(stack)) {
            val id = getId(stack)
            instance().getStorageDiskSync()!!.sendRequest(id)
            val data = instance().getStorageDiskSync()!!.getData(id)
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(TranslationTextComponent("misc.refinedstorage.storage.stored", instance().getQuantityFormatter()!!.format(data.getStored())).setStyle(Styles.GRAY))
                } else {
                    tooltip.add(TranslationTextComponent("misc.refinedstorage.storage.stored_capacity", instance().getQuantityFormatter()!!.format(data.getStored()), instance().getQuantityFormatter()!!.format(data.getCapacity())).setStyle(Styles.GRAY))
                }
            }
            if (flag.isAdvanced()) {
                tooltip.add(StringTextComponent(id.toString()).setStyle(Styles.GRAY))
            }
        }
    }

    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        val storageStack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient && player.isCrouching() && type !== FluidStorageType.CREATIVE) {
            var diskId: UUID? = null
            var disk: IStorageDisk<*>? = null
            if (isValid(storageStack)) {
                diskId = getId(storageStack)
                disk = instance().getStorageDiskManager(world as ServerWorld)!![diskId]
            }

            // Newly created fluid storages won't have a tag yet, so allow invalid disks as well.
            if (disk == null || disk.getStored() == 0) {
                val fluidStoragePart = ItemStack(FluidStoragePartItem.Companion.getByType(type))
                if (!player.inventory.addItemStackToInventory(fluidStoragePart.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), fluidStoragePart)
                }
                val processor = ItemStack(RSItems.BASIC_PROCESSOR)
                if (!player.inventory.addItemStackToInventory(processor.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), processor)
                }
                val bucket = ItemStack(Items.BUCKET)
                if (!player.inventory.addItemStackToInventory(bucket.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), bucket)
                }
                if (disk != null) {
                    instance().getStorageDiskManager(world as ServerWorld)!!.remove(diskId)
                    instance().getStorageDiskManager(world as ServerWorld)!!.markForSaving()
                }
                return ActionResult(ActionResult.SUCCESS, ItemStack(RSBlocks.MACHINE_CASING))
            }
        }
        return ActionResult(ActionResult.PASS, storageStack)
    }

    fun getEntityLifespan(stack: ItemStack?, world: World?): Int {
        return Int.MAX_VALUE
    }

    private fun getId(disk: ItemStack): UUID {
        return disk.tag.getUniqueId(FluidStorageNetworkNode.NBT_ID)
    }

    private fun isValid(disk: ItemStack): Boolean {
        return disk.hasTag() && disk.tag.hasUniqueId(FluidStorageNetworkNode.NBT_ID)
    }

    init {
        type = block.type
        this.setRegistryName(block.getRegistryName())
    }
}