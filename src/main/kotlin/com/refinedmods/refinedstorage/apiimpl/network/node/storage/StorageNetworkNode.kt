package com.refinedmods.refinedstorage.apiimpl.network.node.storage

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.IStorageProvider
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.IStorageScreen
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.nodeimport.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.storage.cacheimport.ItemStorageCache
import com.refinedmods.refinedstorage.apiimpl.storageimport.ItemStorageType
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.tile.StorageTile
import com.refinedmods.refinedstorage.tile.config.IAccessType
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IPrioritizable
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import com.refinedmods.refinedstorage.util.StorageBlockUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.*

class StorageNetworkNode(world: World?, pos: BlockPos?, private val type: ItemStorageType):
        NetworkNode(world!!, pos!!),
        IStorageScreen,
        IStorageProvider,
        IComparable,
        IWhitelistBlacklist,
        IPrioritizable,
        IAccessType,
        IStorageDiskContainerContext
{
    val filters = BaseItemHandler(9).addListener(NetworkNodeInventoryListener(this))
    private var accessType = AccessType.INSERT_EXTRACT
    private var priority = 0
    private var compare = IComparer.COMPARE_NBT
    private var mode = IWhitelistBlacklist.BLACKLIST
    var storageId: UUID = UUID.randomUUID()
        set(value) {
            field = value
            markDirty()
        }
    var storage: IStorageDisk<ItemStack?>? = null
        private set
    override val energyUsage: Int
        get() = when (type) {
            ItemStorageType.ONE_K -> RS.SERVER_CONFIG.storageBlock.oneKUsage
            ItemStorageType.FOUR_K -> RS.SERVER_CONFIG.storageBlock.fourKUsage
            ItemStorageType.SIXTEEN_K -> RS.SERVER_CONFIG.storageBlock.sixteenKUsage
            ItemStorageType.SIXTY_FOUR_K -> RS.SERVER_CONFIG.storageBlock.sixtyFourKUsage
            ItemStorageType.CREATIVE -> RS.SERVER_CONFIG.storageBlock.creativeUsage
            else -> 0
        }

    public override fun onConnectedStateChange(network: INetwork?, state: Boolean, cause: ConnectivityStateChangeCause?) {
        super.onConnectedStateChange(network, state, cause)
        LOGGER.debug("Connectivity state of item storage block at {} changed to {} due to {}", pos, state, cause)
        network!!.nodeGraph!!.runActionWhenPossible(ItemStorageCache.Companion.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
    }

    override fun addItemStorages(storages: List<IStorage<ItemStack?>?>?) {
        if (storage == null) {
            loadStorage()
        }
        storages.add(storage)
    }

    fun addFluidStorages(storages: List<IStorage<FluidInstance?>?>?) {
        // NO OP
    }

    override val id: Identifier
        get() = StorageBlockUtils.getNetworkNodeId(type)

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        tag.putUniqueId(NBT_ID, storageId)
        return tag
    }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        if (tag.hasUniqueId(NBT_ID)) {
            storageId = tag.getUniqueId(NBT_ID)
            loadStorage()
        }
    }

    fun loadStorage() {
        var disk = instance().getStorageDiskManager(world as ServerWorld)!![storageId]
        if (disk == null) {
            instance().getStorageDiskManager(world as ServerWorld)!![storageId] = instance().createDefaultItemDisk(world as ServerWorld, type.capacity).also { disk = it }
            instance().getStorageDiskManager(world as ServerWorld)!!.markForSaving()
        }
        storage = ItemStorageWrapperStorageDisk(this, disk)
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        StackUtils.writeItems(filters, 0, tag)
        tag.putInt(NBT_PRIORITY, priority)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        AccessTypeUtils.writeAccessType(tag, accessType)
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        readItems(filters, 0, tag)
        if (tag.contains(NBT_PRIORITY)) {
            priority = tag.getInt(NBT_PRIORITY)
        }
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE)
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE)
        }
        accessType = AccessTypeUtils.readAccessType(tag)
    }

    override fun getCompare(): Int {
        return compare
    }

    override fun setCompare(compare: Int) {
        this.compare = compare
        markDirty()
    }

    override fun getWhitelistBlacklistMode(): Int {
        return mode
    }

    override fun setWhitelistBlacklistMode(mode: Int) {
        this.mode = mode
        markDirty()
    }

    override val title: Text
        get() = TranslationTextComponent("block.refinedstorage." + type.getName() + "_storage_block")
    override val stored: Long
        get() = StorageTile.STORED.value
    override val capacity: Long
        get() = type.capacity.toLong()

    override fun getAccessType(): AccessType {
        return accessType
    }

    override fun setAccessType(value: AccessType) {
        accessType = value
        if (network != null) {
            network!!.itemStorageCache!!.invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED)
        }
        markDirty()
    }

    override fun getPriority(): Int {
        return priority
    }

    override fun setPriority(priority: Int) {
        this.priority = priority
        markDirty()
        if (network != null) {
            network!!.itemStorageCache!!.sort()
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        val ONE_K_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "1k_storage_block")
        @kotlin.jvm.JvmField
        val FOUR_K_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "4k_storage_block")
        @kotlin.jvm.JvmField
        val SIXTEEN_K_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "16k_storage_block")
        @kotlin.jvm.JvmField
        val SIXTY_FOUR_K_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "64k_storage_block")
        @kotlin.jvm.JvmField
        val CREATIVE_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "creative_storage_block")
        private val LOGGER = LogManager.getLogger(StorageNetworkNode::class.java)
        private const val NBT_PRIORITY = "Priority"
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        const val NBT_ID = "Id"
    }
}