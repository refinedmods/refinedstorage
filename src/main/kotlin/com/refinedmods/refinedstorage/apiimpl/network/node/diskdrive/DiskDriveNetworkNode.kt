package com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.IStorageProvider
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.tile.DiskDriveTile
import com.refinedmods.refinedstorage.tile.config.*
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.createStorages
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import java.util.function.Function


class DiskDriveNetworkNode(world: World, pos: BlockPos?) : NetworkNode(world, pos!!), IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IType, IAccessType, IStorageDiskContainerContext {
    private var ticksSinceBlockUpdateRequested = 0
    private var blockUpdateRequested = false
    private val itemFilters = BaseItemHandler(9).addListener(NetworkNodeInventoryListener(this))
    private val fluidFilters = FluidInventory(9).addListener(NetworkNodeFluidInventoryListener(this))
    val itemDisks = arrayOfNulls<IStorageDisk<*>?>(8)
    val fluidDisks = arrayOfNulls<IStorageDisk<*>?>(8)
    private val disks = BaseItemHandler(8)
            .addValidator(StorageDiskItemValidator())
            .addListener(NetworkNodeInventoryListener(this))
            .addListener { handler: BaseItemHandler, slot: Int, reading: Boolean ->
                if (!world.isClient) {
                    createStorages(
                            world as ServerWorld,
                            handler.getStackInSlot(slot),
                            slot,
                            itemDisks,
                            fluidDisks,
                            Function<IStorageDisk<ItemStack>?, IStorageDisk<*>?> { s: IStorageDisk<ItemStack>? -> ItemDriveWrapperStorageDisk(this@DiskDriveNetworkNode, s) },
                            Function<IStorageDisk<FluidInstance>?, IStorageDisk<*>?> { s: IStorageDisk<FluidInstance>? -> FluidDriveWrapperStorageDisk(this@DiskDriveNetworkNode, s) }
                    )
                    if (network != null) {
                        network!!.itemStorageCache!!.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
                        network!!.fluidStorageCache!!.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
                    }
                    if (!reading) {
                        WorldUtils.updateBlock(world, pos)
                    }
                }
            }
    private var accessType = AccessType.INSERT_EXTRACT
    private var priority = 0
    private var compare = IComparer.COMPARE_NBT
    private var mode = IWhitelistBlacklist.BLACKLIST
    private var type = IType.ITEMS
    override val energyUsage: Int
        get() {
            var usage = RS.SERVER_CONFIG.diskDrive.usage
            for (storage in itemDisks) {
                if (storage != null) {
                    usage += RS.SERVER_CONFIG.diskDrive.diskUsage
                }
            }
            for (storage in fluidDisks) {
                if (storage != null) {
                    usage += RS.SERVER_CONFIG.diskDrive.diskUsage
                }
            }
            return usage
        }

    override fun update() {
        super.update()
        if (blockUpdateRequested) {
            ++ticksSinceBlockUpdateRequested
            if (ticksSinceBlockUpdateRequested > DISK_STATE_UPDATE_THROTTLE) {
                WorldUtils.updateBlock(world, pos)
                blockUpdateRequested = false
                ticksSinceBlockUpdateRequested = 0
            }
        } else {
            ticksSinceBlockUpdateRequested = 0
        }
    }

    fun requestBlockUpdate() {
        blockUpdateRequested = true
    }

    public override fun onConnectedStateChange(network: INetwork?, state: Boolean, cause: ConnectivityStateChangeCause?) {
        super.onConnectedStateChange(network, state, cause)
        LOGGER.debug("Connectivity state of disk drive at {} changed to {} due to {}", pos, state, cause)
        network!!.nodeGraph!!.runActionWhenPossible(ItemStorageCache.Companion.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
        network.nodeGraph!!.runActionWhenPossible(FluidStorageCache.Companion.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
        WorldUtils.updateBlock(world, pos)
    }

    override fun addItemStorages(storages: List<IStorage<ItemStack?>?>?) {
        for (storage in itemDisks) {
            if (storage != null) {
                storages.add(storage)
            }
        }
    }

    fun addFluidStorages(storages: List<IStorage<FluidInstance?>?>?) {
        for (storage in fluidDisks) {
            if (storage != null) {
                storages.add(storage)
            }
        }
    }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        readItems(disks, 0, tag)
    }

    override val id: Identifier
        get() = ID

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        StackUtils.writeItems(disks, 0, tag)
        return tag
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        StackUtils.writeItems(itemFilters, 1, tag)
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt())
        tag.putInt(NBT_PRIORITY, priority)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        tag.putInt(NBT_TYPE, type)
        AccessTypeUtils.writeAccessType(tag, accessType)
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        readItems(itemFilters, 1, tag)
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS))
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
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE)
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

    override fun getAccessType(): AccessType {
        return accessType
    }

    override fun setAccessType(value: AccessType) {
        accessType = value
        if (network != null) {
            network!!.fluidStorageCache!!.invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED)
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
            network!!.fluidStorageCache!!.sort()
        }
    }

    val diskState: Array<DiskState?>
        get() {
            val diskStates = arrayOfNulls<DiskState>(8)
            for (i in 0..7) {
                var state = DiskState.NONE
                if (itemDisks[i] != null || fluidDisks[i] != null) {
                    state = if (!canUpdate()) {
                        DiskState.DISCONNECTED
                    } else {
                        DiskState.Companion.get(
                                if (itemDisks[i] != null) itemDisks[i]!!.getStored() else fluidDisks[i]!!.getStored(),
                                if (itemDisks[i] != null) itemDisks[i]!!.capacity else fluidDisks[i]!!.capacity
                        )
                    }
                }
                diskStates[i] = state
            }
            return diskStates
        }

    fun getDisks(): IItemHandler {
        return disks
    }

    override fun getType(): Int {
        return if (world.isClient) DiskDriveTile.TYPE.value else type
    }

    override fun setType(type: Int) {
        this.type = type
        markDirty()
    }

    override fun getItemFilters(): IItemHandlerModifiable {
        return itemFilters
    }

    override fun getFluidFilters(): FluidInventory {
        return fluidFilters
    }

    override val drops: IItemHandler
        get() = disks

    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "disk_drive")
        private const val NBT_PRIORITY = "Priority"
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_TYPE = "Type"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
        private const val DISK_STATE_UPDATE_THROTTLE = 30
        private val LOGGER = LogManager.getLogger(DiskDriveNetworkNode::class.java)
    }
}