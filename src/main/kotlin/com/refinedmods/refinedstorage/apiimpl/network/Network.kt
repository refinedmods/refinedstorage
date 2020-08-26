package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraph
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphListener
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.api.network.security.ISecurityManager
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingManager
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.FluidGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.item.NetworkItemManager
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.security.SecurityManager
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cacheimport.ItemStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker
import com.refinedmods.refinedstorage.apiimpl.storage.trackerimport.ItemStorageTracker
import com.refinedmods.refinedstorage.block.ControllerBlock
import com.refinedmods.refinedstorage.energy.BaseEnergyStorage
import com.refinedmods.refinedstorage.tile.ControllerTile
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.util.StackUtils
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import java.util.function.Predicate

class Network(world: World, pos: BlockPos, type: NetworkType) : INetwork, IRedstoneConfigurable {
    val itemGridHandler: IItemGridHandler = ItemGridHandler(this)
    val fluidGridHandler: IFluidGridHandler = FluidGridHandler(this)
    val networkItemManager: INetworkItemManager = NetworkItemManager(this)
    private val nodeGraph: INetworkNodeGraph = NetworkNodeGraph(this)
    private val craftingManager: ICraftingManager = CraftingManager(this)
    private val securityManager: ISecurityManager = SecurityManager(this)
    private val itemStorage: IStorageCache<ItemStack> = ItemStorageCache(this)
    private val itemStorageTracker: ItemStorageTracker = ItemStorageTracker(Runnable { markDirty() })
    private val fluidStorage: IStorageCache<FluidInstance> = FluidStorageCache(this)
    private val fluidStorageTracker: FluidStorageTracker = FluidStorageTracker(Runnable { markDirty() })
    private val energy: BaseEnergyStorage = BaseEnergyStorage(RS.SERVER_CONFIG.getController().getCapacity(), RS.SERVER_CONFIG.getController().getMaxTransfer(), 0)
    private val root: RootNetworkNode
    private val pos: BlockPos
    private val world: World
    private val type: NetworkType
    private var lastEnergyType: ControllerBlock.EnergyType = ControllerBlock.EnergyType.OFF
    var energyUsage = 0
        private set
    private var redstoneMode: RedstoneMode = RedstoneMode.IGNORE
    private var redstonePowered = false
    private var amILoaded = false
    private var throttlingDisabled = true // Will be enabled after first update
    private var couldRun = false
    private var ticksSinceUpdateChanged = 0
    private var ticks = 0
    fun getRoot(): RootNetworkNode {
        return root
    }

    fun getEnergy(): BaseEnergyStorage {
        return energy
    }

    val position: BlockPos
        get() = pos

    override fun canRun(): Boolean {
        return amILoaded && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(redstonePowered)
    }

    fun setRedstonePowered(redstonePowered: Boolean) {
        this.redstonePowered = redstonePowered
    }

    override fun getNodeGraph(): INetworkNodeGraph {
        return nodeGraph
    }

    override fun getSecurityManager(): ISecurityManager {
        return securityManager
    }

    override fun getCraftingManager(): ICraftingManager {
        return craftingManager
    }

    override fun update() {
        if (!world.isClient) {
            if (ticks == 0) {
                redstonePowered = world.isReceivingRedstonePower(pos)
            }
            ++ticks
            amILoaded = world.isBlockPresent(pos)
            updateEnergyUsage()
            if (canRun()) {
                craftingManager.update()
                if (!craftingManager.getTasks().isEmpty()) {
                    markDirty()
                }
            }
            if (type === NetworkType.NORMAL) {
                if (!RS.SERVER_CONFIG.getController().getUseEnergy()) {
                    energy.setStored(energy.getMaxEnergyStored())
                } else {
                    energy.extractEnergyBypassCanExtract(energyUsage, false)
                }
            } else if (type === NetworkType.CREATIVE) {
                energy.setStored(energy.getMaxEnergyStored())
            }
            val canRun = canRun()
            if (couldRun != canRun) {
                ++ticksSinceUpdateChanged
                if ((if (canRun) ticksSinceUpdateChanged > THROTTLE_INACTIVE_TO_ACTIVE else ticksSinceUpdateChanged > THROTTLE_ACTIVE_TO_INACTIVE) || throttlingDisabled) {
                    ticksSinceUpdateChanged = 0
                    couldRun = canRun
                    throttlingDisabled = false
                    LOGGER.debug("Network at position {} changed running state to {}, causing an invalidation of the node graph", pos, couldRun)
                    nodeGraph.invalidate(Action.PERFORM, world, pos)
                    securityManager.invalidate()
                }
            } else {
                ticksSinceUpdateChanged = 0
            }
            val energyType: ControllerBlock.EnergyType = energyType
            if (lastEnergyType !== energyType) {
                lastEnergyType = energyType
                val state: BlockState = world.getBlockState(pos)
                if (state.getBlock() is ControllerBlock) {
                    world.setBlockState(pos, state.with(ControllerBlock.ENERGY_TYPE, energyType))
                }
            }
        }
    }

    override fun onRemoved() {
        for (task in craftingManager.getTasks()) {
            task.onCancelled()
        }
        nodeGraph.disconnectAll()
    }

    val itemStorageCache: IStorageCache<ItemStack>
        get() = itemStorage
    val fluidStorageCache: IStorageCache<FluidInstance>
        get() = fluidStorage

    @Nonnull
    override fun insertItem(@Nonnull stack: ItemStack?, size: Int, action: Action?): ItemStack? {
        var size = size
        if (stack.isEmpty()) {
            return stack
        }
        if (itemStorage.getStorages().isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size)
        }
        var remainder: ItemStack? = stack
        var inserted = 0
        var insertedExternally = 0
        for (storage in itemStorage.getStorages()) {
            if (storage.getAccessType() === AccessType.EXTRACT) {
                continue
            }
            val storedPre: Int = storage.getStored()
            remainder = storage.insert(remainder, size, action)
            if (action === Action.PERFORM) {
                inserted += storage.getCacheDelta(storedPre, size, remainder)
            }
            if (remainder.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    insertedExternally += size
                }
                break
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getCount() && storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    insertedExternally += size - remainder.getCount()
                }
                size = remainder.getCount()
            }
        }
        if (action === Action.PERFORM && inserted - insertedExternally > 0) {
            itemStorage.add(stack, inserted - insertedExternally, false, false)
        }
        return remainder
    }

    @Nonnull
    override fun extractItem(@Nonnull stack: ItemStack?, size: Int, flags: Int, action: Action?, filter: Predicate<IStorage<ItemStack?>?>?): ItemStack? {
        if (stack.isEmpty()) {
            return stack
        }
        var received = 0
        var extractedExternally = 0
        var newStack: ItemStack = ItemStack.EMPTY
        for (storage in itemStorage.getStorages()) {
            var took: ItemStack = ItemStack.EMPTY
            if (filter!!.test(storage) && storage.getAccessType() !== AccessType.INSERT) {
                took = storage.extract(stack, size - received, flags, action)
            }
            if (!took.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    extractedExternally += took.getCount()
                }
                if (newStack.isEmpty()) {
                    newStack = took
                } else {
                    newStack.grow(took.getCount())
                }
                received += took.getCount()
            }
            if (size == received) {
                break
            }
        }
        if (newStack.getCount() - extractedExternally > 0 && action === Action.PERFORM) {
            itemStorage.remove(newStack, newStack.getCount() - extractedExternally, false)
        }
        return newStack
    }

    @Nonnull
    fun insertFluid(@Nonnull stack: FluidInstance?, size: Int, action: Action?): FluidInstance? {
        var size = size
        if (stack.isEmpty()) {
            return stack
        }
        if (fluidStorage.getStorages().isEmpty()) {
            return StackUtils.copy(stack, size)
        }
        var remainder: FluidInstance? = stack
        var inserted = 0
        var insertedExternally = 0
        for (storage in fluidStorage.getStorages()) {
            if (storage.getAccessType() === AccessType.EXTRACT) {
                continue
            }
            val storedPre: Int = storage.getStored()
            remainder = storage.insert(remainder, size, action)
            if (action === Action.PERFORM) {
                inserted += storage.getCacheDelta(storedPre, size, remainder)
            }
            if (remainder.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    insertedExternally += size
                }
                break
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getAmount() && storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    insertedExternally += size - remainder.getAmount()
                }
                size = remainder.getAmount()
            }
        }
        if (action === Action.PERFORM && inserted - insertedExternally > 0) {
            fluidStorage.add(stack, inserted - insertedExternally, false, false)
        }
        return remainder
    }

    @Nonnull
    fun extractFluid(@Nonnull stack: FluidInstance?, size: Int, flags: Int, action: Action?, filter: Predicate<IStorage<FluidInstance?>?>?): FluidInstance? {
        if (stack.isEmpty()) {
            return stack
        }
        var received = 0
        var extractedExternally = 0
        var newStack: FluidInstance = FluidInstance.EMPTY
        for (storage in fluidStorage.getStorages()) {
            var took: FluidInstance = FluidInstance.EMPTY
            if (filter!!.test(storage) && storage.getAccessType() !== AccessType.INSERT) {
                took = storage.extract(stack, size - received, flags, action)
            }
            if (!took.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
                    (storage as IExternalStorage<*>).update(this)
                    extractedExternally += took.getAmount()
                }
                if (newStack.isEmpty()) {
                    newStack = took
                } else {
                    newStack.grow(took.getAmount())
                }
                received += took.getAmount()
            }
            if (size == received) {
                break
            }
        }
        if (newStack.getAmount() - extractedExternally > 0 && action === Action.PERFORM) {
            fluidStorage.remove(newStack, newStack.getAmount() - extractedExternally, false)
        }
        return newStack
    }

    override fun getItemStorageTracker(): IStorageTracker<ItemStack> {
        return itemStorageTracker
    }

    override fun getFluidStorageTracker(): IStorageTracker<FluidInstance> {
        return fluidStorageTracker
    }

    override fun getWorld(): World {
        return world
    }

    override fun readFromNbt(tag: CompoundTag?): INetwork? {
        if (tag.contains(NBT_ENERGY)) {
            energy.setStored(tag.getInt(NBT_ENERGY))
        }
        redstoneMode = RedstoneMode.read(tag)
        craftingManager.readFromNbt(tag)
        if (tag.contains(NBT_ITEM_STORAGE_TRACKER)) {
            itemStorageTracker.readFromNbt(tag.getList(NBT_ITEM_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        if (tag.contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(tag.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        return this
    }

    override fun writeToNbt(tag: CompoundTag?): CompoundTag? {
        tag.putInt(NBT_ENERGY, energy.getEnergyStored())
        redstoneMode.write(tag)
        craftingManager.writeToNbt(tag)
        tag.put(NBT_ITEM_STORAGE_TRACKER, itemStorageTracker.serializeNbt())
        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt())
        return tag
    }

    override fun markDirty() {
        API.instance().getNetworkManager(world as ServerWorld).markForSaving()
    }

    @kotlin.jvm.JvmStatic
    val energyType: ControllerBlock.EnergyType
        get() = if (!redstoneMode.isEnabled(redstonePowered)) {
            ControllerBlock.EnergyType.OFF
        } else getEnergyType(energy.getEnergyStored(), energy.getMaxEnergyStored())

    override fun getRedstoneMode(): RedstoneMode {
        return redstoneMode
    }

    override fun setRedstoneMode(mode: RedstoneMode) {
        redstoneMode = mode
        markDirty()
    }

    private fun updateEnergyUsage() {
        if (!redstoneMode.isEnabled(redstonePowered)) {
            energyUsage = 0
            return
        }
        var usage: Int = RS.SERVER_CONFIG.getController().getBaseUsage()
        for (node in nodeGraph.all()) {
            if (node.isActive) {
                usage += node.energyUsage
            }
        }
        energyUsage = usage
    }

    val energyStorage: IEnergyStorage
        get() = energy

    override fun getType(): NetworkType {
        return type
    }

    companion object {
        private const val THROTTLE_INACTIVE_TO_ACTIVE = 20
        private const val THROTTLE_ACTIVE_TO_INACTIVE = 4
        private const val NBT_ENERGY = "Energy"
        private const val NBT_ITEM_STORAGE_TRACKER = "ItemStorageTracker"
        private const val NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker"
        private val LOGGER = LogManager.getLogger(Network::class.java)
        @kotlin.jvm.JvmStatic
        fun getEnergyScaled(stored: Int, capacity: Int, scale: Int): Int {
            return (stored.toFloat() / capacity.toFloat() * scale.toFloat()).toInt()
        }

        fun getEnergyType(stored: Int, capacity: Int): ControllerBlock.EnergyType {
            val energy = getEnergyScaled(stored, capacity, 100)
            if (energy <= 0) {
                return ControllerBlock.EnergyType.OFF
            } else if (energy <= 10) {
                return ControllerBlock.EnergyType.NEARLY_OFF
            } else if (energy <= 20) {
                return ControllerBlock.EnergyType.NEARLY_ON
            }
            return ControllerBlock.EnergyType.ON
        }
    }

    init {
        this.pos = pos
        this.world = world
        this.type = type
        this.root = RootNetworkNode(this, world, pos)
        nodeGraph.addListener(INetworkNodeGraphListener {
            val tile: BlockEntity = world.getBlockEntity(pos)
            if (tile is ControllerTile) {
                (tile as ControllerTile).getDataManager().sendParameterToWatchers(ControllerTile.NODES)
            }
        })
    }
}