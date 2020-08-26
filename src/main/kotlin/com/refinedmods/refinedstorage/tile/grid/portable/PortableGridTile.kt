package com.refinedmods.refinedstorage.tile.grid.portable

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSearchBoxMode
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSize
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingDirection
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isValidSortingType
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableFluidGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableItemGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker
import com.refinedmods.refinedstorage.block.PortableGridBlock
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.inventory.listener.TileInventoryListener
import com.refinedmods.refinedstorage.item.WirelessGridItem
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.tile.BaseTile
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.createParameter
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.read
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import com.refinedmods.refinedstorage.tile.grid.GridTile
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.Direction
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

class PortableGridTile(private val type: PortableGridBlockItem.Type) : BaseTile(if (type === PortableGridBlockItem.Type.CREATIVE) RSTiles.CREATIVE_PORTABLE_GRID else RSTiles.PORTABLE_GRID), IGrid, IPortableGrid, IRedstoneConfigurable, IStorageDiskContainerContext {
    private var energyStorage: EnergyStorage = createEnergyStorage(0)
    private val energyStorageCap: LazyOptional<EnergyStorage> = LazyOptional.of({ energyStorage })
    private override var redstoneMode = RedstoneMode.IGNORE
    private override var sortingType = 0
    private override var sortingDirection = 0
    private override var searchBoxMode = 0
    private override var tabSelected = 0
    private override var tabPage = 0
    private override var size = 0
    private var clientGridType: GridType? = null
    override val filters: List<IFilter<*>> = ArrayList()
    override val tabs: List<IGridTab> = ArrayList()
    private override val filter = FilterItemHandler(filters, tabs).addListener(TileInventoryListener(this)) as FilterItemHandler
    override val disk = BaseItemHandler(1)
            .addValidator(StorageDiskItemValidator())
            .addListener(TileInventoryListener(this))
            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler?, slot: Int, reading: Boolean ->
                if (world != null && !world.isClient) {
                    loadStorage()
                    if (!reading) {
                        updateState()
                        WorldUtils.updateBlock(world, pos) // Re-send grid type
                    }
                }
            })

    @get:Nullable
    @Nullable
    override var storage: IStorageDisk<*>? = null
        private set

    @get:Nullable
    @Nullable
    override var cache: IStorageCache<*>? = null
        private set

    @get:Nullable
    override val itemHandler = PortableItemGridHandler(this, this)

    @get:Nullable
    override val fluidHandler = PortableFluidGridHandler(this)
    private override var diskState = PortableGridDiskState.NONE
    private var active = false
    private val storageTracker: ItemStorageTracker = ItemStorageTracker(::markDirty)
    private override val fluidStorageTracker = FluidStorageTracker(Runnable { markDirty() })
    private var enchants: ListTag? = null
    private fun loadStorage() {
        val diskStack: ItemStack = disk.getStackInSlot(0)
        if (diskStack.isEmpty) {
            storage = null
            cache = null
        } else {
            val disk = instance().getStorageDiskManager(world as ServerWorld?)!!.getByStack(disk.getStackInSlot(0))
            if (disk != null) {
                val type = (disk.getStackInSlot(0).getItem() as IStorageDiskProvider).getType()
                when (type) {
                    StorageType.ITEM -> {
                        storage = PortableItemStorageDisk(disk, this)
                        cache = PortableItemStorageCache(this)
                    }
                    StorageType.FLUID -> {
                        storage = PortableFluidStorageDisk(disk, this)
                        cache = PortableFluidStorageCache(this)
                    }
                }
                storage!!.setSettings(IStorageDiskListener { updateState() }, this@PortableGridTile)
            } else {
                storage = null
                cache = null
            }
        }
        if (cache != null) {
            cache!!.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
        }
    }

    fun onLoad() {
        super.onLoad()
        loadStorage()
        active = isGridActive
        diskState = getDiskState()
    }

    fun applyDataFromItemToTile(stack: ItemStack) {
        sortingType = WirelessGridItem.getSortingType(stack)
        sortingDirection = WirelessGridItem.getSortingDirection(stack)
        searchBoxMode = WirelessGridItem.getSearchBoxMode(stack)
        tabSelected = WirelessGridItem.getTabSelected(stack)
        tabPage = WirelessGridItem.getTabPage(stack)
        size = WirelessGridItem.getSize(stack)
        val energyStorage: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null)
        this.energyStorage = createEnergyStorage(if (energyStorage != null) energyStorage.getEnergyStored() else 0)
        if (stack.hasTag()) {
            for (i in 0..3) {
                readItems(filter, i, stack.tag)
            }
            readItems(disk, 4, stack.tag)
            redstoneMode = read(stack.tag!!)
            if (stack.tag!!.contains(PortableGrid.Companion.NBT_STORAGE_TRACKER)) {
                storageTracker.readFromNbt(stack.tag!!.getList(PortableGrid.Companion.NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
            }
            if (stack.tag!!.contains(PortableGrid.Companion.NBT_FLUID_STORAGE_TRACKER)) {
                fluidStorageTracker.readFromNbt(stack.tag!!.getList(PortableGrid.Companion.NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
            }
            if (stack.tag!!.contains(NBT_ENCHANTMENTS)) {
                enchants = stack.tag!!.getList(NBT_ENCHANTMENTS, Constants.NBT.TAG_COMPOUND)
            }
        }
        markDirty()
    }

    fun applyDataFromTileToItem(stack: ItemStack) {
        stack.tag = CompoundTag()
        stack.tag!!.putInt(GridNetworkNode.NBT_SORTING_DIRECTION, sortingDirection)
        stack.tag!!.putInt(GridNetworkNode.NBT_SORTING_TYPE, sortingType)
        stack.tag!!.putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, searchBoxMode)
        stack.tag!!.putInt(GridNetworkNode.NBT_SIZE, size)
        stack.tag!!.putInt(GridNetworkNode.NBT_TAB_SELECTED, tabSelected)
        stack.tag!!.putInt(GridNetworkNode.NBT_TAB_PAGE, tabPage)
        stack.tag!!.put(PortableGrid.Companion.NBT_STORAGE_TRACKER, storageTracker.serializeNbt())
        stack.tag!!.put(PortableGrid.Companion.NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt())
        if (enchants != null) {
            stack.tag!!.put(NBT_ENCHANTMENTS, enchants)
        }
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent({ itemEnergy -> itemEnergy.receiveEnergy(energyStorage.getEnergyStored(), false) })
        for (i in 0..3) {
            StackUtils.writeItems(filter, i, stack.tag)
        }
        StackUtils.writeItems(disk, 4, stack.tag)
        redstoneMode.write(stack.tag!!)
    }

    private fun createEnergyStorage(energyStored: Int): EnergyStorage {
        return EnergyStorage(
                RS.SERVER_CONFIG.portableGrid.getCapacity(),
                RS.SERVER_CONFIG.portableGrid.getCapacity(),
                RS.SERVER_CONFIG.portableGrid.getCapacity(),
                energyStored
        )
    }

    override val gridType: GridType
        get() = if (clientGridType != null) clientGridType!! else serverGridType
    private val serverGridType: GridType
        private get() = if (disk.getStackInSlot(0).isEmpty() || (disk.getStackInSlot(0).getItem() as IStorageDiskProvider).getType() === StorageType.ITEM) GridType.NORMAL else GridType.FLUID

    @get:Nullable
    override val storageCache: IStorageCache<*>?
        get() = if (storage != null) cache else null

    override fun createListener(player: ServerPlayerEntity?): IStorageCacheListener<*> {
        return if (serverGridType === GridType.FLUID) PortableFluidGridStorageCacheListener(this, player) else PortableItemGridStorageCacheListener(this, player)
    }

    override fun addCraftingListener(listener: ICraftingGridListener?) {
        // NO OP
    }

    override fun removeCraftingListener(listener: ICraftingGridListener?) {
        // NO OP
    }

    override val title: Text
        get() = TranslationTextComponent("gui.refinedstorage.portable_grid")
    override val viewType: Int
        get() = -1

    override fun getSortingType(): Int {
        return if (world.isClient) SORTING_TYPE.value else sortingType
    }

    override fun getSortingDirection(): Int {
        return if (world.isClient) SORTING_DIRECTION.value else sortingDirection
    }

    override fun getSearchBoxMode(): Int {
        return if (world.isClient) SEARCH_BOX_MODE.value else searchBoxMode
    }

    override fun getTabSelected(): Int {
        return if (world.isClient) TAB_SELECTED.value else tabSelected
    }

    override fun getTabPage(): Int {
        return if (world.isClient) TAB_PAGE.value else Math.min(tabPage, totalTabPages)
    }

    override val totalTabPages: Int
        get() = Math.floor(Math.max(0, tabs.size - 1).toFloat() / IGrid.TABS_PER_PAGE as Float.toDouble()).toInt()

    override fun getSize(): Int {
        return if (world.isClient) SIZE.value else size
    }

    fun setSortingType(sortingType: Int) {
        this.sortingType = sortingType
    }

    fun setSortingDirection(sortingDirection: Int) {
        this.sortingDirection = sortingDirection
    }

    fun setSearchBoxMode(searchBoxMode: Int) {
        this.searchBoxMode = searchBoxMode
    }

    fun setTabSelected(tabSelected: Int) {
        this.tabSelected = tabSelected
    }

    fun setTabPage(page: Int) {
        tabPage = page
    }

    fun setSize(size: Int) {
        this.size = size
    }

    override fun onViewTypeChanged(type: Int) {
        // NO OP
    }

    override fun onSortingTypeChanged(type: Int) {
        TileDataManager.Companion.setParameter(SORTING_TYPE, type)
    }

    override fun onSortingDirectionChanged(direction: Int) {
        TileDataManager.Companion.setParameter(SORTING_DIRECTION, direction)
    }

    override fun onSearchBoxModeChanged(searchBoxMode: Int) {
        TileDataManager.Companion.setParameter(SEARCH_BOX_MODE, searchBoxMode)
    }

    override fun onSizeChanged(size: Int) {
        TileDataManager.Companion.setParameter(SIZE, size)
    }

    override fun onTabSelectionChanged(tab: Int) {
        TileDataManager.Companion.setParameter(TAB_SELECTED, tab)
    }

    override fun onTabPageChanged(page: Int) {
        if (page >= 0 && page <= totalTabPages) {
            TileDataManager.Companion.setParameter(TAB_PAGE, page)
        }
    }

    override fun getFilter(): IItemHandlerModifiable {
        return filter
    }

    override val itemStorageTracker: IStorageTracker<ItemStack?>
        get() = storageTracker

    override fun getFluidStorageTracker(): IStorageTracker<FluidInstance?> {
        return fluidStorageTracker
    }

    override val craftingMatrix: CraftingInventory?
        get() = null
    override val craftingResult: CraftingResultInventory?
        get() = null

    override fun onCraftingMatrixChanged() {
        // NO OP
    }

    override fun onCrafted(player: PlayerEntity?, @Nullable availableItems: IStackList<ItemStack?>?, @Nullable usedItems: IStackList<ItemStack?>?) {
        // NO OP
    }

    override fun onClear(player: PlayerEntity?) {
        // NO OP
    }

    override fun onCraftedShift(player: PlayerEntity?) {
        // NO OP
    }

    override fun onRecipeTransfer(player: PlayerEntity?, recipe: Array<Array<ItemStack?>?>?) {
        // NO OP
    }

    override fun onClosed(player: PlayerEntity?) {
        // NO OP
    }

    private fun hasDisk(): Boolean {
        return !disk.getStackInSlot(0).isEmpty()
    }

    override val isGridActive: Boolean
        get() {
            if (world.isClient) {
                val state = world!!.getBlockState(pos)
                return if (state.block is PortableGridBlock) {
                    state.get(PortableGridBlock.ACTIVE)
                } else false
            }
            if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && type !== PortableGridBlockItem.Type.CREATIVE && energyStorage.getEnergyStored() <= RS.SERVER_CONFIG.portableGrid.getOpenUsage()) {
                return false
            }
            return if (!hasDisk()) {
                false
            } else redstoneMode.isEnabled(world.isReceivingRedstonePower(pos))
        }
    val slotId: Int
        get() = -1

    override fun drainEnergy(energy: Int) {
        if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && type !== PortableGridBlockItem.Type.CREATIVE &&
                redstoneMode.isEnabled(world.isReceivingRedstonePower(pos))) {
            energyStorage.extractEnergy(energy, false)
            updateState()
        }
    }

    override val energy: Int
        get() = if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && type !== PortableGridBlockItem.Type.CREATIVE) {
            energyStorage.getEnergyStored()
        } else RS.SERVER_CONFIG.portableGrid.getCapacity()

    override fun getDiskState(): PortableGridDiskState {
        if (!hasDisk()) {
            return PortableGridDiskState.NONE
        }
        if (!isGridActive) {
            return PortableGridDiskState.DISCONNECTED
        }
        val stored = if (storage != null) storage!!.getStored() else 0
        val capacity = if (storage != null) storage!!.capacity else 0
        return if (stored == capacity) {
            PortableGridDiskState.FULL
        } else if ((stored.toFloat() / capacity.toFloat() * 100f).toInt() >= DiskState.DISK_NEAR_CAPACITY_THRESHOLD) {
            PortableGridDiskState.NEAR_CAPACITY
        } else {
            PortableGridDiskState.NORMAL
        }
    }

    fun updateState() {
        val newDiskState = getDiskState()
        if (diskState != newDiskState) {
            diskState = newDiskState
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(PortableGridBlock.DISK_STATE, diskState))
        }
        val isActive = isGridActive
        if (active != isActive) {
            active = isActive
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(PortableGridBlock.ACTIVE, active))
        }
    }

    fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        tag.putInt(GridNetworkNode.NBT_SORTING_DIRECTION, sortingDirection)
        tag.putInt(GridNetworkNode.NBT_SORTING_TYPE, sortingType)
        tag.putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, searchBoxMode)
        tag.putInt(GridNetworkNode.NBT_SIZE, size)
        tag.putInt(GridNetworkNode.NBT_TAB_SELECTED, tabSelected)
        tag.putInt(GridNetworkNode.NBT_TAB_PAGE, tabPage)
        StackUtils.writeItems(disk, 0, tag)
        StackUtils.writeItems(filter, 1, tag)
        tag.putInt(NBT_ENERGY, energyStorage.getEnergyStored())
        redstoneMode.write(tag)
        tag.put(NBT_STORAGE_TRACKER, storageTracker.serializeNbt())
        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt())
        if (enchants != null) {
            tag.put(NBT_ENCHANTMENTS, enchants)
        }
        return tag
    }

    fun read(blockState: BlockState?, tag: CompoundTag) {
        super.read(blockState, tag)
        if (tag.contains(GridNetworkNode.NBT_SORTING_DIRECTION)) {
            sortingDirection = tag.getInt(GridNetworkNode.NBT_SORTING_DIRECTION)
        }
        if (tag.contains(GridNetworkNode.NBT_SORTING_TYPE)) {
            sortingType = tag.getInt(GridNetworkNode.NBT_SORTING_TYPE)
        }
        if (tag.contains(GridNetworkNode.NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInt(GridNetworkNode.NBT_SEARCH_BOX_MODE)
        }
        if (tag.contains(GridNetworkNode.NBT_SIZE)) {
            size = tag.getInt(GridNetworkNode.NBT_SIZE)
        }
        if (tag.contains(GridNetworkNode.NBT_TAB_SELECTED)) {
            tabSelected = tag.getInt(GridNetworkNode.NBT_TAB_SELECTED)
        }
        if (tag.contains(GridNetworkNode.NBT_TAB_PAGE)) {
            tabPage = tag.getInt(GridNetworkNode.NBT_TAB_PAGE)
        }
        readItems(disk, 0, tag)
        readItems(filter, 1, tag)
        if (tag.contains(NBT_ENERGY)) {
            energyStorage = createEnergyStorage(tag.getInt(NBT_ENERGY))
        }
        redstoneMode = read(tag)
        if (tag.contains(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(tag.getList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        if (tag.contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(tag.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        if (tag.contains(NBT_ENCHANTMENTS)) {
            enchants = tag.getList(NBT_ENCHANTMENTS, Constants.NBT.TAG_COMPOUND)
        }
    }

    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        tag.putInt(NBT_TYPE, serverGridType.ordinal)
        return super.writeUpdate(tag)
    }

    override fun readUpdate(tag: CompoundTag?) {
        super.readUpdate(tag)
        clientGridType = GridType.values()[tag!!.getInt(NBT_TYPE)]
    }

    @Nonnull
    fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityEnergy.ENERGY) {
            energyStorageCap.cast()
        } else super.getCapability(cap, direction)
    }

    fun onOpened() {
        drainEnergy(RS.SERVER_CONFIG.portableGrid.getOpenUsage())
    }

    override fun getRedstoneMode(): RedstoneMode {
        return redstoneMode
    }

    override fun setRedstoneMode(mode: RedstoneMode) {
        redstoneMode = mode
        markDirty()
    }

    override fun getAccessType(): AccessType? {
        return AccessType.INSERT_EXTRACT
    }

    companion object {
        val REDSTONE_MODE = createParameter<PortableGridTile>()
        private val SORTING_DIRECTION = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getSortingDirection() }, BiConsumer { t: PortableGridTile, v: Int ->
            if (isValidSortingDirection(v)) {
                t.setSortingDirection(v)
                t.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> GridTile.Companion.trySortGrid(initial) })
        private val SORTING_TYPE = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getSortingType() }, BiConsumer { t: PortableGridTile, v: Int ->
            if (isValidSortingType(v)) {
                t.setSortingType(v)
                t.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> GridTile.Companion.trySortGrid(initial) })
        private val SEARCH_BOX_MODE = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getSearchBoxMode() }, BiConsumer { t: PortableGridTile, v: Int ->
            if (isValidSearchBoxMode(v)) {
                t.setSearchBoxMode(v)
                t.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getSearchField().setMode(p) }) })
        private val SIZE = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getSize() }, BiConsumer { t: PortableGridTile, v: Int ->
            if (isValidSize(v)) {
                t.setSize(v)
                t.markDirty()
            }
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { obj: BaseScreen<*> -> obj.init() }) })
        private val TAB_SELECTED = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getTabSelected() }, BiConsumer { t: PortableGridTile, v: Int ->
            t.setTabSelected(if (v == t.getTabSelected()) -1 else v)
            t.markDirty()
        }, TileDataParameterClientListener<Int> { initial: Boolean, p: Int? -> BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() }) })
        private val TAB_PAGE = TileDataParameter(DataSerializers.VARINT, 0, Function { obj: PortableGridTile -> obj.getTabPage() }, BiConsumer { t: PortableGridTile, v: Int ->
            if (v >= 0 && v <= t.totalTabPages) {
                t.setTabPage(v)
                t.markDirty()
            }
        })
        private const val NBT_STORAGE_TRACKER = "StorageTracker"
        private const val NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker"
        private const val NBT_TYPE = "Type"
        private const val NBT_ENERGY = "Energy"
        private const val NBT_ENCHANTMENTS = "ench" // @Volatile: minecraft specific nbt key
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
        dataManager.addWatchedParameter(SORTING_DIRECTION)
        dataManager.addWatchedParameter(SORTING_TYPE)
        dataManager.addWatchedParameter(SEARCH_BOX_MODE)
        dataManager.addWatchedParameter(SIZE)
        dataManager.addWatchedParameter(TAB_SELECTED)
        dataManager.addWatchedParameter(TAB_PAGE)
    }
}