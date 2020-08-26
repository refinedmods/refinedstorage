package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import org.apache.commons.lang3.tuple.Pair
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Function

object StackUtils {
    @JvmField
    val EMPTY_BUCKET = ItemStack(Items.BUCKET)
    private const val NBT_INVENTORY = "Inventory_%d"
    private const val NBT_SLOT = "Slot"
    private val LOGGER = LogManager.getLogger(StackUtils::class.java)

    // @Volatile: from PacketByteBuf#writeItemStack, with some tweaks to allow int stack counts
    @JvmStatic
    fun writeItemStack(buf: PacketByteBuf, stack: ItemStack) {
        if (stack.isEmpty) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            val item = stack.item
            buf.writeVarInt(Item.getIdFromItem(item))
            buf.writeInt(stack.count)
            var tag: CompoundTag? = null
            if (item.isDamageable || item.shouldSyncTag()) {
                tag = stack.tag
            }
            buf.writeCompoundTag(tag)
        }
    }

    // @Volatile: from PacketByteBuf#readItemStack, with some tweaks to allow int stack counts
    @JvmStatic
    fun readItemStack(buf: PacketByteBuf): ItemStack {
        return if (!buf.readBoolean()) {
            ItemStack.EMPTY
        } else {
            val id: Int = buf.readVarInt()
            val count: Int = buf.readInt()
            val stack = ItemStack(Item.getItemById(id), count)
            stack.readShareTag(buf.readCompoundTag())
            stack
        }
    }

    @JvmStatic
    fun writeItemGridStack(buf: PacketByteBuf, stack: ItemStack, id: UUID?, @Nullable otherId: UUID?, craftable: Boolean, @Nullable entry: StorageTrackerEntry?) {
        writeItemStack(buf, stack)
        buf.writeBoolean(craftable)
        buf.writeUniqueId(id)
        buf.writeBoolean(otherId != null)
        if (otherId != null) {
            buf.writeUniqueId(otherId)
        }
        if (entry == null) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            buf.writeLong(entry.time)
            buf.writeString(entry.name)
        }
    }

    @JvmStatic
    fun readItemGridStack(buf: PacketByteBuf): ItemGridStack {
        val stack = readItemStack(buf)
        val craftable: Boolean = buf.readBoolean()
        val id: UUID = buf.readUniqueId()
        var otherId: UUID? = null
        if (buf.readBoolean()) {
            otherId = buf.readUniqueId()
        }
        var entry: StorageTrackerEntry? = null
        if (buf.readBoolean()) {
            entry = StorageTrackerEntry(buf.readLong(), PacketByteBufUtils.readString(buf))
        }
        return ItemGridStack(id, otherId, stack, craftable, entry)
    }

    @JvmStatic
    fun writeFluidGridStack(buf: PacketByteBuf, stack: FluidInstance, id: UUID?, @Nullable otherId: UUID?, craftable: Boolean, @Nullable entry: StorageTrackerEntry?) {
        stack.writeToPacket(buf)
        buf.writeBoolean(craftable)
        buf.writeUniqueId(id)
        buf.writeBoolean(otherId != null)
        if (otherId != null) {
            buf.writeUniqueId(otherId)
        }
        if (entry == null) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            buf.writeLong(entry.time)
            buf.writeString(entry.name)
        }
    }

    @JvmStatic
    fun readFluidGridStack(buf: PacketByteBuf): FluidGridStack {
        val stack: FluidInstance = FluidInstance.readFromPacket(buf)
        val craftable: Boolean = buf.readBoolean()
        val id: UUID = buf.readUniqueId()
        var otherId: UUID? = null
        if (buf.readBoolean()) {
            otherId = buf.readUniqueId()
        }
        var entry: StorageTrackerEntry? = null
        if (buf.readBoolean()) {
            entry = StorageTrackerEntry(buf.readLong(), PacketByteBufUtils.readString(buf))
        }
        return FluidGridStack(id, otherId, stack, entry, craftable)
    }

    @JvmStatic
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
    }

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
    }

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

    private const val NBT_ITEM_ID = "Id"
    private const val NBT_ITEM_QUANTITY = "Quantity"
    private const val NBT_ITEM_NBT = "NBT"
    private const val NBT_ITEM_CAPS = "Caps"
    @JvmStatic
    fun serializeStackToNbt(stack: ItemStack): CompoundTag {
        val dummy = CompoundTag()
        val itemTag = CompoundTag()
        itemTag.putString(NBT_ITEM_ID, stack.item.getRegistryName().toString())
        itemTag.putInt(NBT_ITEM_QUANTITY, stack.count)
        if (stack.hasTag()) {
            itemTag.put(NBT_ITEM_NBT, stack.tag)
        }
        stack.write(dummy)
        if (dummy.contains("ForgeCaps")) {
            itemTag.put(NBT_ITEM_CAPS, dummy.get("ForgeCaps"))
        }
        dummy.remove("ForgeCaps")
        return itemTag
    }

    @JvmStatic
    fun deserializeStackFromNbt(tag: CompoundTag): ItemStack {
        val item: Item
        if (tag.contains(NBT_ITEM_ID)) {
            item = ForgeRegistries.ITEMS.getValue(Identifier(tag.getString(NBT_ITEM_ID)))
            if (item == null) {
                LOGGER.warn("Could not deserialize item from string ID, it no longer exists: " + tag.getString(NBT_ITEM_ID))
            }
        } else {
            throw IllegalStateException("Cannot deserialize ItemStack: no " + NBT_ITEM_ID + " tag was found!")
        }
        if (item == null) {
            return ItemStack.EMPTY
        }
        val stack = ItemStack(
                item,
                tag.getInt(NBT_ITEM_QUANTITY),
                if (tag.contains(NBT_ITEM_CAPS)) tag.getCompound(NBT_ITEM_CAPS) else null
        )
        stack.tag = if (tag.contains(NBT_ITEM_NBT)) tag.getCompound(NBT_ITEM_NBT) else null
        return stack
    }
}