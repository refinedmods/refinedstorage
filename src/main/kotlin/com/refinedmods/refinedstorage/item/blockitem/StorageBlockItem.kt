package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.refinedmods.refinedstorage.apiimpl.storageimport.ItemStorageType
import com.refinedmods.refinedstorage.block.StorageBlock
import com.refinedmods.refinedstorage.item.StoragePartItem
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.util.*

class StorageBlockItem(block: StorageBlock) : BaseBlockItem(block, Properties().group(RS.MAIN_GROUP)) {
    private val type: ItemStorageType
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
        if (!world.isClient && player.isCrouching() && type !== ItemStorageType.CREATIVE) {
            var diskId: UUID? = null
            var disk: IStorageDisk<*>? = null
            if (isValid(storageStack)) {
                diskId = getId(storageStack)
                disk = instance().getStorageDiskManager(world as ServerWorld)!![diskId]
            }

            // Newly created storages won't have a tag yet, so allow invalid disks as well.
            if (disk == null || disk.getStored() == 0) {
                val storagePart = ItemStack(StoragePartItem.Companion.getByType(type))
                if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), storagePart)
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
        return disk.tag.getUniqueId(StorageNetworkNode.NBT_ID)
    }

    private fun isValid(disk: ItemStack): Boolean {
        return disk.hasTag() && disk.tag.hasUniqueId(StorageNetworkNode.NBT_ID)
    }

    init {
        type = block.type
        this.setRegistryName(block.getRegistryName())
    }
}