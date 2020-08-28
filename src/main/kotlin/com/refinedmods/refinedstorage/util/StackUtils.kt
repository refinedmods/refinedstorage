@file:Suppress("UNREACHABLE_CODE")

package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.util.PacketBufUtils.readOptional
import com.refinedmods.refinedstorage.util.PacketBufUtils.readOptionalUuid
import com.refinedmods.refinedstorage.util.PacketBufUtils.writeOptional
import com.refinedmods.refinedstorage.util.PacketBufUtils.writeOptionalUuid
import com.refinedmods.refinedstorage.util.RegistryUtil.getOr
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import java.util.*

object StackUtils {
    val EMPTY_BUCKET = ItemStack(Items.BUCKET)
    private const val NBT_INVENTORY = "Inventory_%d"
    private const val NBT_SLOT = "Slot"
    private val LOGGER = LogManager.getLogger(StackUtils::class.java)

    // @Volatile: from PacketByteBuf#writeItemStack, with some tweaks to allow int stack counts
    fun writeItemStack(buf: PacketByteBuf, stack: ItemStack) {
        if (stack.isEmpty) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            val item = stack.item
            buf.writeVarInt(Item.getRawId(item))
            buf.writeInt(stack.count)
            var tag: CompoundTag? = null
            if (item.isDamageable || item.shouldSyncTagToClient()) {
                tag = stack.tag
            }
            buf.writeCompoundTag(tag)
        }
    }

    // @Volatile: from PacketByteBuf#readItemStack, with some tweaks to allow int stack counts
    fun readItemStack(buf: PacketByteBuf): ItemStack {
        return if (!buf.readBoolean()) {
            ItemStack.EMPTY
        } else {
            val id: Int = buf.readVarInt()
            val count: Int = buf.readInt()
            val stack = ItemStack(Item.byRawId(id), count)
            stack.tag = buf.readCompoundTag()
            stack
        }
    }

    fun writeItemGridStack(
        buf: PacketByteBuf,
        stack: ItemStack,
        id: UUID,
        otherId: UUID?,
        craftable: Boolean,
        entry: StorageTrackerEntry?
    ) {
        writeItemStack(buf, stack)
        writeGridStack(buf, id, otherId, craftable, entry)
    }

    private fun writeGridStack(
        buf: PacketByteBuf,
        id: UUID,
        otherId: UUID?,
        craftable: Boolean,
        entry: StorageTrackerEntry?
    ) {
        buf.writeBoolean(craftable)
        buf.writeUuid(id)
        buf.writeOptionalUuid(otherId)
        buf.writeOptional(entry) {
            buf.writeLong(it.time)
            buf.writeString(it.name)
        }
    }

    fun readItemGridStack(buf: PacketByteBuf): Nothing { //Todo: Change to ItemGridStack
        val stack = readItemStack(buf)
        val craftable: Boolean = buf.readBoolean()
        val id: UUID = buf.readUuid()
        var otherId: UUID? = buf.readOptionalUuid()
        var entry: StorageTrackerEntry? = buf.readOptional {
            StorageTrackerEntry(readLong(), readString())
        }
        TODO("return ItemGridStack(id, otherId, stack, craftable, entry)")
    }

    fun writeFluidGridStack(
        buf: PacketByteBuf,
        stack: FluidInstance,
        id: UUID,
        otherId: UUID?,
        craftable: Boolean,
        entry: StorageTrackerEntry?
    ) {
        //stack.writeToPacket(buf)
        writeGridStack(buf, id, otherId, craftable, entry)
    }

    fun readFluidGridStack(buf: PacketByteBuf): Nothing {// TODO: replace with FluidGridStack {
        val stack: FluidInstance = TODO("FluidInstance.readFromPacket(buf)")
        val craftable: Boolean = buf.readBoolean()
        val id: UUID = buf.readUuid()
        var otherId: UUID? = buf.readOptionalUuid()
        var entry: StorageTrackerEntry? = buf.readOptional {
            StorageTrackerEntry(readLong(), readString())
        }
        TODO("return FluidGridStack(id, otherId, stack, entry, craftable)")
    }

    /*
fun createStorages(world: ServerWorld?, diskStack: ItemStack, slot: Int, itemDisks: Array<IStorageDisk<ItemStack?>?>, fluidDisks: Array<IStorageDisk<FluidInstance?>?>, itemDiskWrapper: Function<IStorageDisk<ItemStack?>?, IStorageDisk<*>?>, fluidDiskWrapper: Function<IStorageDisk<FluidInstance?>?, IStorageDisk<*>?>) {
    if (diskStack.isEmpty) {
        itemDisks[slot] = null
        fluidDisks[slot] = null
    } else {
        val disk = instance().getStorageDiskManager(world).getByStack(diskStack)
        if (disk != null) {
            when ((diskStack.item as IStorageDiskProvider).type) {
                StorageType.ITEM -> {
                    itemDisks[slot] = itemDiskWrapper.apply(disk)
                }
                StorageType.FLUID -> {
                    fluidDisks[slot] = fluidDiskWrapper.apply(disk)
                }
            }
        } else {
            itemDisks[slot] = null
            fluidDisks[slot] = null
        }
    }
}*/
/*
    fun writeItems(handler: IItemHandler, id: Int, tag: CompoundTag, serializer: Function<ItemStack, CompoundTag>) {
        val tagList = ListTag()
        for (i in 0 until handler.getSlots()) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                val stackTag: CompoundTag = serializer.apply(handler.getStackInSlot(i))
                stackTag.putInt(NBT_SLOT, i)
                tagList.add(stackTag)
            }
        }
        tag.put(String.format(NBT_INVENTORY, id), tagList)
    }

    fun writeItems(handler: IItemHandler, id: Int, tag: CompoundTag) {
        writeItems(handler, id, tag, Function<ItemStack, CompoundTag> { stack: ItemStack -> stack.write(CompoundTag()) })
    }

    fun readItems(handler: IItemHandlerModifiable, id: Int, tag: CompoundTag, deserializer: Function<CompoundTag?, ItemStack?>) {
        val name = String.format(NBT_INVENTORY, id)
        if (tag.contains(name)) {
            val tagList: ListTag = tag.getList(name, Constants.NBT.TAG_COMPOUND)
            for (i in 0 until tagList.size()) {
                val slot: Int = tagList.getCompound(i).getInt(NBT_SLOT)
                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.setStackInSlot(slot, deserializer.apply(tagList.getCompound(i)))
                }
            }
        }
    }

    fun readItems(handler: IItemHandlerModifiable, id: Int, tag: CompoundTag) {
        readItems(handler, id, tag, ItemStack::read)
    }

    fun readItems(handler: BaseItemHandler, id: Int, tag: CompoundTag) {
        handler.setReading(true)
        readItems(handler, id, tag, ItemStack::read)
        handler.setReading(false)
    }

    fun writeItems(inventory: IInventory, id: Int, tag: CompoundTag) {
        val tagList = ListTag()
        for (i in 0 until inventory.getSizeInventory()) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                val stackTag = CompoundTag()
                stackTag.putInt(NBT_SLOT, i)
                inventory.getStackInSlot(i).write(stackTag)
                tagList.add(stackTag)
            }
        }
        tag.put(String.format(NBT_INVENTORY, id), tagList)
    }

    fun readItems(inventory: IInventory, id: Int, tag: CompoundTag) {
        val name = String.format(NBT_INVENTORY, id)
        if (tag.contains(name)) {
            val tagList: ListTag = tag.getList(name, Constants.NBT.TAG_COMPOUND)
            for (i in 0 until tagList.size()) {
                val slot: Int = tagList.getCompound(i).getInt(NBT_SLOT)
                val stack: ItemStack = ItemStack.read(tagList.getCompound(i))
                if (!stack.isEmpty) {
                    inventory.setInventorySlotContents(slot, stack)
                }
            }
        }
    }

    fun copy(stack: FluidInstance, size: Int): FluidInstance {
        val copy: FluidInstance = stack.copy()
        copy.setAmount(size)
        return copy
    }

    fun copy(@Nullable stack: FluidInstance?): FluidInstance? {
        return if (stack == null) null else stack.copy()
    }*/
/*
    @JvmStatic
    fun getFluid(stack: ItemStack, simulate: Boolean): Pair<ItemStack, FluidInstance> {
        var stack = stack
        if (stack.isEmpty) {
            return Pair.of<ItemStack, FluidInstance>(ItemStack.EMPTY, FluidInstance.EMPTY)
        }
        if (stack.count > 1) {
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1)
        }
        val handler: IFluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElse(null)
        if (handler != null) {
            val result: FluidInstance = handler.drain(FluidAttributes.BUCKET_VOLUME, if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE)
            return Pair.of(handler.getContainer(), result)
        }
        return Pair.of<ItemStack, FluidInstance>(ItemStack.EMPTY, FluidInstance.EMPTY)
    }
*/
    private const val NBT_ITEM_ID = "Id"
    private const val NBT_ITEM_QUANTITY = "Quantity"
    private const val NBT_ITEM_NBT = "NBT"
    private const val NBT_ITEM_CAPS = "Caps"

    fun serializeStackToNbt(stack: ItemStack): CompoundTag {
        val dummy = CompoundTag()
        val itemTag = CompoundTag()
        itemTag.putString(NBT_ITEM_ID, stack.item.name.toString())
        itemTag.putInt(NBT_ITEM_QUANTITY, stack.count)
        if (stack.hasTag()) {
            itemTag.put(NBT_ITEM_NBT, stack.tag)
        }
        stack.tag = dummy
        // TODO check
        if (dummy.contains("ForgeCaps")) {
            itemTag.put(NBT_ITEM_CAPS, dummy.get("ForgeCaps"))
        }
        dummy.remove("ForgeCaps")
        return itemTag
    }

    fun deserializeStackFromNbt(tag: CompoundTag): ItemStack {
        val item: Item =
            if (tag.contains(NBT_ITEM_ID)) {
                Registry.ITEM.getOr(Identifier(tag.getString(NBT_ITEM_ID))) {
                    LOGGER.warn(
                        "Could not deserialize item from string ID, it no longer exists: " + tag.getString(
                            NBT_ITEM_ID
                        )
                    )
                    return ItemStack.EMPTY
                }
            } else {
                error("Cannot deserialize ItemStack: no $NBT_ITEM_ID tag was found!")
            }

        val stack = ItemStack(
            item,
            tag.getInt(NBT_ITEM_QUANTITY)
        )

        // TODO: forgecaps?:  if (tag.contains(NBT_ITEM_CAPS)) tag.getCompound(NBT_ITEM_CAPS) else null

        stack.tag = if (tag.contains(NBT_ITEM_NBT)) tag.getCompound(NBT_ITEM_NBT) else null
        return stack
    }
}