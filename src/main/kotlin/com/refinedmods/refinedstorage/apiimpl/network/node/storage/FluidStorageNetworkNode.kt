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
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.network.node.IStorageScreen
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.nodeimport.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storageimport.FluidStorageType
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.tile.FluidStorageTile
import com.refinedmods.refinedstorage.tile.config.IAccessType
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IPrioritizable
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import com.refinedmods.refinedstorage.util.FluidStorageBlockUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fluids.FluidInstance
import org.apache.logging.log4j.LogManager
import java.util.*


class FluidStorageNetworkNode(world: World?, pos: BlockPos?, private val type: FluidStorageType) : NetworkNode(world!!, pos!!), IStorageScreen, IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IAccessType, IStorageDiskContainerContext {
    val filters = FluidInventory(9).addListener(NetworkNodeFluidInventoryListener(this))
    private var accessType = AccessType.INSERT_EXTRACT
    private var priority = 0
    private var compare = IComparer.COMPARE_NBT
    private var mode = IWhitelistBlacklist.BLACKLIST
    var storageId: UUID = UUID.randomUUID()
        set(value) {
            field = value
            markDirty()
        }
    private var storage: IStorageDisk<FluidInstance?>? = null
    override val energyUsage: Int
        get() = when (type) {
            FluidStorageType.SIXTY_FOUR_K -> RS.SERVER_CONFIG.fluidStorageBlock.sixtyFourKUsage
            FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K -> RS.SERVER_CONFIG.fluidStorageBlock.twoHundredFiftySixKUsage
            FluidStorageType.THOUSAND_TWENTY_FOUR_K -> RS.SERVER_CONFIG.fluidStorageBlock.thousandTwentyFourKUsage
            FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K -> RS.SERVER_CONFIG.fluidStorageBlock.fourThousandNinetySixKUsage
            FluidStorageType.CREATIVE -> RS.SERVER_CONFIG.fluidStorageBlock.creativeUsage
            else -> 0
        }

    public override fun onConnectedStateChange(network: INetwork?, state: Boolean, cause: ConnectivityStateChangeCause?) {
        super.onConnectedStateChange(network, state, cause)
        LOGGER.debug("Connectivity state of fluid storage block at {} changed to {} due to {}", pos, state, cause)
        network!!.nodeGraph!!.runActionWhenPossible(FluidStorageCache.Companion.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
    }

    override fun addItemStorages(storages: List<IStorage<ItemStack?>?>?) {
        // NO OP
    }

    fun addFluidStorages(storages: List<IStorage<FluidInstance?>?>?) {
        if (storage == null) {
            loadStorage()
        }
        storages.add(storage)
    }

    override val id: Identifier
        get() = FluidStorageBlockUtils.getNetworkNodeId(type)

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
            instance().getStorageDiskManager(world as ServerWorld)!![storageId] = instance().createDefaultFluidDisk(world as ServerWorld, type.capacity).also { disk = it }
            instance().getStorageDiskManager(world as ServerWorld)!!.markForSaving()
        }
        storage = FluidStorageWrapperStorageDisk(this, disk)
    }

    fun getStorage(): IStorageDisk<FluidInstance?>? {
        return storage
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        tag.put(NBT_FILTERS, filters.writeToNbt())
        tag.putInt(NBT_PRIORITY, priority)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        AccessTypeUtils.writeAccessType(tag, accessType)
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        if (tag.contains(NBT_FILTERS)) {
            filters.readFromNbt(tag.getCompound(NBT_FILTERS))
        }
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
        get() = TranslationTextComponent("block.refinedstorage." + type.getName() + "_fluid_storage_block")
    override val stored: Long
        get() = FluidStorageTile.STORED.value
    override val capacity: Long
        get() = type.capacity.toLong()

    override fun getAccessType(): AccessType {
        return accessType
    }

    override fun setAccessType(value: AccessType) {
        accessType = value
        if (network != null) {
            network!!.fluidStorageCache!!.invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED)
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
            network!!.fluidStorageCache!!.sort()
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        val SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "64k_fluid_storage_block")
        @kotlin.jvm.JvmField
        val TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "256k_fluid_storage_block")
        @kotlin.jvm.JvmField
        val THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "1024k_fluid_storage_block")
        @kotlin.jvm.JvmField
        val FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "4096k_fluid_storage_block")
        @kotlin.jvm.JvmField
        val CREATIVE_FLUID_STORAGE_BLOCK_ID: Identifier = Identifier(RS.ID, "creative_fluid_storage_block")
        private val LOGGER = LogManager.getLogger(FluidStorageNetworkNode::class.java)
        private const val NBT_PRIORITY = "Priority"
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_FILTERS = "Filters"
        const val NBT_ID = "Id"
    }
}