package com.refinedmods.refinedstorage.tile.grid.portable

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableFluidGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableItemGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.item.WirelessGridItem
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem
import com.refinedmods.refinedstorage.network.grid.PortableGridSettingsUpdateMessage
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.util.Constants
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*

class PortableGrid(@field:Nullable @param:Nullable val player: PlayerEntity?, val stack: ItemStack, val slotId: Int) : IGrid, IPortableGrid, IStorageDiskContainerContext {
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

    override var sortingType: Int
        private set
    override var sortingDirection: Int
        private set
    override var searchBoxMode: Int
        private set
    override var tabSelected: Int
        private set
    private override var tabPage: Int
    override var size: Int
        private set
    private val storageTracker: ItemStorageTracker = ItemStorageTracker { stack.tag!!.put(NBT_STORAGE_TRACKER, itemStorageTracker.serializeNbt()) }
    override val fluidStorageTracker = FluidStorageTracker(Runnable { stack.tag!!.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt()) })
    override val filters: List<IFilter<*>> = ArrayList()
    override val tabs: List<IGridTab> = ArrayList()
    private override val filter = FilterItemHandler(filters, tabs)
            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler?, slot: Int, reading: Boolean ->
                if (!stack.hasTag()) {
                    stack.tag = CompoundTag()
                }
                StackUtils.writeItems(handler, 0, stack.tag)
            }) as FilterItemHandler
    override val disk = BaseItemHandler(1)
            .addValidator(StorageDiskItemValidator())
            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler, slot: Int, reading: Boolean ->
                if (player != null && !player.world.isClient) {
                    val diskStack: ItemStack = handler.getStackInSlot(slot)
                    if (diskStack.isEmpty) {
                        storage = null
                        cache = null
                    } else {
                        val disk = instance().getStorageDiskManager(player.world as ServerWorld)!!.getByStack(disk.getStackInSlot(0))
                        if (disk != null) {
                            val type = (disk.getStackInSlot(0).getItem() as IStorageDiskProvider).getType()
                            when (type) {
                                StorageType.ITEM -> {
                                    storage = PortableItemStorageDisk(disk, this@PortableGrid)
                                    cache = PortableItemStorageCache(this@PortableGrid)
                                }
                                StorageType.FLUID -> {
                                    storage = PortableFluidStorageDisk(disk, this@PortableGrid)
                                    cache = PortableFluidStorageCache(this@PortableGrid)
                                }
                            }
                            storage!!.setSettings(null, this@PortableGrid)
                        } else {
                            storage = null
                            cache = null
                        }
                    }
                    if (cache != null) {
                        cache!!.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
                    }
                    StackUtils.writeItems(handler, 4, stack.tag)
                }
            })

    fun onOpen() {
        drainEnergy(RS.SERVER_CONFIG.portableGrid.getOpenUsage())
    }

    override fun drainEnergy(energy: Int) {
        if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && (stack.item as PortableGridBlockItem).type !== PortableGridBlockItem.Type.CREATIVE) {
            val storage: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null)
            if (storage != null) {
                storage.extractEnergy(energy, false)
            }
        }
    }

    override val energy: Int
        get() {
            if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && (stack.item as PortableGridBlockItem).type !== PortableGridBlockItem.Type.CREATIVE) {
                val storage: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null)
                return if (storage == null) RS.SERVER_CONFIG.portableGrid.getCapacity() else storage.getEnergyStored()
            }
            return RS.SERVER_CONFIG.portableGrid.getCapacity()
        }
    override val gridType: GridType
        get() = if (disk.getStackInSlot(0).isEmpty() || (disk.getStackInSlot(0).getItem() as IStorageDiskProvider).getType() === StorageType.ITEM) GridType.NORMAL else GridType.FLUID

    @get:Nullable
    override val storageCache: IStorageCache<*>?
        get() = if (storage != null) cache else null

    override fun createListener(player: ServerPlayerEntity?): IStorageCacheListener<*> {
        return if (gridType === GridType.FLUID) PortableFluidGridStorageCacheListener(this, player) else PortableItemGridStorageCacheListener(this, player)
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

    override fun getTabPage(): Int {
        return Math.min(tabPage, totalTabPages)
    }

    override val totalTabPages: Int
        get() = Math.floor(Math.max(0, tabs.size - 1).toFloat() / IGrid.TABS_PER_PAGE as Float.toDouble()).toInt()

    override fun onViewTypeChanged(type: Int) {
        // NO OP
    }

    override fun onSortingTypeChanged(type: Int) {
        RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, sortingDirection, type, searchBoxMode, size, tabSelected, getTabPage()))
        sortingType = type
        BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() })
    }

    override fun onSortingDirectionChanged(direction: Int) {
        RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, direction, sortingType, searchBoxMode, size, tabSelected, getTabPage()))
        sortingDirection = direction
        BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() })
    }

    override fun onSearchBoxModeChanged(searchBoxMode: Int) {
        RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, sortingDirection, sortingType, searchBoxMode, size, tabSelected, getTabPage()))
        this.searchBoxMode = searchBoxMode
    }

    override fun onSizeChanged(size: Int) {
        RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, sortingDirection, sortingType, searchBoxMode, size, tabSelected, getTabPage()))
        this.size = size
        BaseScreen.executeLater(GridScreen::class.java, { obj: BaseScreen<*> -> obj.init() })
    }

    override fun onTabSelectionChanged(tab: Int) {
        tabSelected = if (tab == tabSelected) -1 else tab
        RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, sortingDirection, sortingType, searchBoxMode, size, tabSelected, getTabPage()))
        BaseScreen.executeLater(GridScreen::class.java, { grid -> grid.getView().sort() })
    }

    override fun onTabPageChanged(page: Int) {
        if (page >= 0 && page <= totalTabPages) {
            RS.NETWORK_HANDLER.sendToServer(PortableGridSettingsUpdateMessage(viewType, sortingDirection, sortingType, searchBoxMode, size, tabSelected, page))
            tabPage = page
        }
    }

    override fun getFilter(): IItemHandlerModifiable {
        return filter
    }

    override val itemStorageTracker: IStorageTracker<ItemStack?>
        get() = storageTracker
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
        if (!player!!.entityWorld.isRemote) {
            StackUtils.writeItems(disk, 4, stack.tag)
        }
    }

    private fun hasDisk(): Boolean {
        return !disk.getStackInSlot(0).isEmpty()
    }

    override val isGridActive: Boolean
        get() = if (RS.SERVER_CONFIG.portableGrid.getUseEnergy() && (stack.item as PortableGridBlockItem).type !== PortableGridBlockItem.Type.CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY).orElse(null).getEnergyStored() <= RS.SERVER_CONFIG.portableGrid.getOpenUsage()) {
            false
        } else hasDisk()

    @get:Nullable
    private val diskId: UUID?
        private get() = if (!hasDisk()) null else (disk.getStackInSlot(0).getItem() as IStorageDiskProvider).getId(disk.getStackInSlot(0))
    private val stored: Int
        private get() {
            instance().getStorageDiskSync()!!.sendRequest(diskId)
            val data = instance().getStorageDiskSync()!!.getData(diskId)
            return data?.getStored() ?: 0
        }
    private val capacity: Int
        private get() {
            instance().getStorageDiskSync()!!.sendRequest(diskId)
            val data = instance().getStorageDiskSync()!!.getData(diskId)
            return data?.getCapacity() ?: 0
        }
    override val diskState: PortableGridDiskState
        get() {
            if (!hasDisk()) {
                return PortableGridDiskState.NONE
            }
            if (!isGridActive) {
                return PortableGridDiskState.DISCONNECTED
            }
            val stored = stored
            val capacity = capacity
            return if (stored == capacity) {
                PortableGridDiskState.FULL
            } else if ((stored.toFloat() / capacity.toFloat() * 100f).toInt() >= DiskState.DISK_NEAR_CAPACITY_THRESHOLD) {
                PortableGridDiskState.NEAR_CAPACITY
            } else {
                PortableGridDiskState.NORMAL
            }
        }

    override fun getAccessType(): AccessType? {
        return AccessType.INSERT_EXTRACT
    }

    companion object {
        const val NBT_STORAGE_TRACKER = "StorageTracker"
        const val NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker"
    }

    init {
        sortingType = WirelessGridItem.getSortingType(stack)
        sortingDirection = WirelessGridItem.getSortingDirection(stack)
        searchBoxMode = WirelessGridItem.getSearchBoxMode(stack)
        tabSelected = WirelessGridItem.getTabSelected(stack)
        tabPage = WirelessGridItem.getTabPage(stack)
        size = WirelessGridItem.getSize(stack)
        if (!stack.hasTag()) {
            stack.tag = CompoundTag()
        }
        if (stack.tag!!.contains(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(stack.tag!!.getList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        if (stack.tag!!.contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(stack.tag!!.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
        }
        readItems(disk, 4, stack.tag)
        readItems(filter, 0, stack.tag)
    }
}