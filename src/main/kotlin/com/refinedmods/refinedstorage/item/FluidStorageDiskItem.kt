package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.util.*

class FluidStorageDiskItem(type: FluidStorageType) : Item(Properties().group(RS.MAIN_GROUP).maxStackSize(1)), IStorageDiskProvider {
    private val type: FluidStorageType
    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (!world.isClient && !stack.hasTag()) {
            val id = UUID.randomUUID()
            instance().getStorageDiskManager(world as ServerWorld)!![id] = instance().createDefaultFluidDisk(world as ServerWorld, getCapacity(stack))
            instance().getStorageDiskManager(world as ServerWorld)!!.markForSaving()
            setId(stack, id)
        }
    }

    fun addInformation(stack: ItemStack?, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag) {
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
        val diskStack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient && player.isCrouching() && type !== FluidStorageType.CREATIVE) {
            val disk = instance().getStorageDiskManager(world as ServerWorld)!!.getByStack(diskStack)
            if (disk != null && disk.getStored() == 0) {
                val storagePart = ItemStack(FluidStoragePartItem.Companion.getByType(type), diskStack.count)
                if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), storagePart)
                }
                instance().getStorageDiskManager(world as ServerWorld)!!.remove(getId(diskStack))
                instance().getStorageDiskManager(world as ServerWorld)!!.markForSaving()
                return ActionResult(ActionResult.SUCCESS, ItemStack(RSItems.STORAGE_HOUSING))
            }
        }
        return ActionResult(ActionResult.PASS, diskStack)
    }

    fun getEntityLifespan(stack: ItemStack?, world: World?): Int {
        return Int.MAX_VALUE
    }

    override fun getId(disk: ItemStack?): UUID? {
        return disk!!.tag.getUniqueId(NBT_ID)
    }

    override fun setId(disk: ItemStack?, id: UUID?) {
        disk!!.tag = CompoundTag()
        disk.tag.putUniqueId(NBT_ID, id)
    }

    override fun isValid(disk: ItemStack?): Boolean {
        return disk!!.hasTag() && disk.tag.hasUniqueId(NBT_ID)
    }

    override fun getCapacity(disk: ItemStack?): Int {
        return type.getCapacity()
    }

    override fun getType(): StorageType? {
        return StorageType.FLUID
    }

    companion object {
        private const val NBT_ID = "Id"
    }

    init {
        this.type = type
        this.setRegistryName(RS.ID, type.getName().toString() + "_fluid_storage_disk")
    }
}