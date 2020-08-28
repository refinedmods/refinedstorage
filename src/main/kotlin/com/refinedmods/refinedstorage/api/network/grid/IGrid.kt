package com.refinedmods.refinedstorage.api.network.grid

import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.api.util.IStackList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text


/**
 * Represents a grid.
 */
interface IGrid {
    /**
     * @return the grid type
     */
    val gridType: GridType?

    /**
     * @param player the player to create a listener for
     * @return a listener for this grid, will be attached to the storage cache in [.getStorageCache]
     */
    fun createListener(player: ServerPlayerEntity): IStorageCacheListener<*>

    /**
     * @return the storage cache for this grid, or null if this grid is unavailable
     */
    val storageCache: IStorageCache<*>?

    /**
     * @return the item grid handler, or null if there is no handler available
     */
    val itemHandler: IItemGridHandler?

    /**
     * @return the fluid grid handler, or null if there is no handler available
     */
    val fluidHandler: IFluidGridHandler?

    /**
     * @param listener the listener
     */
    fun addCraftingListener(listener: ICraftingGridListener) {}

    /**
     * @param listener the listener
     */
    fun removeCraftingListener(listener: ICraftingGridListener) {}

    /**
     * @return the title
     */
    val title: Text?

    /**
     * @return the view type
     */
    val viewType: Int

    /**
     * @return the sorting type
     */
    val sortingType: Int

    /**
     * @return the sorting direction
     */
    val sortingDirection: Int

    /**
     * @return the search box mode
     */
    val searchBoxMode: Int

    /**
     * @return the current tab that is selected
     */
    val tabSelected: Int

    /**
     * @return the current page that the tab is on
     */
    val tabPage: Int

    /**
     * @return the total amount of tab pages
     */
    val totalTabPages: Int

    /**
     * @return the size mode
     */
    val size: Int

    /**
     * @param type the new view type
     */
    fun onViewTypeChanged(type: Int)

    /**
     * @param type the new sorting type
     */
    fun onSortingTypeChanged(type: Int)

    /**
     * @param direction the new direction
     */
    fun onSortingDirectionChanged(direction: Int)

    /**
     * @param searchBoxMode the new search box mode
     */
    fun onSearchBoxModeChanged(searchBoxMode: Int)

    /**
     * @param size the new size mode
     */
    fun onSizeChanged(size: Int)

    /**
     * @param tab the new selected tab
     */
    fun onTabSelectionChanged(tab: Int)

    /**
     * @param page the new selected page
     */
    fun onTabPageChanged(page: Int)

    /**
     * @return the filters
     */
    val filters: List<IFilter<*>>

    /**
     * @return the tabs
     */
    val tabs: List<IGridTab>

    /**
     * @return the inventory of the filters
     */
    val filter: Inventory

    /**
     * @return the crafting matrix, or null if not a crafting grid
     */
    val craftingMatrix: CraftingInventory?

    /**
     * @return the crafting result inventory, or null if not a crafting grid
     */
    val craftingResult: CraftingResultInventory?

    /**
     * Called when the crafting matrix changes.
     */
    fun onCraftingMatrixChanged()

    /**
     * Called when an item is crafted in a crafting grid.
     *
     * @param player         the player that crafted the item
     * @param availableItems the items available for shift crafting
     * @param usedItems      the items used by shift crafting
     */
    fun onCrafted(player: PlayerEntity?, availableItems: IStackList<ItemStack?>?, usedItems: IStackList<ItemStack>)

    /**
     * Called when the clear button is pressed in the pattern grid or crafting grid.
     */
    fun onClear(player: PlayerEntity?)

    /**
     * Called when an item is crafted with shift click (up to 64 items) in a crafting grid.
     *
     * @param player the player that crafted the item
     */
    fun onCraftedShift(player: PlayerEntity?)

    /**
     * Called when a JEI recipe transfer occurs.
     *
     * @param player the player
     * @param recipe a 9*x array stack array, where x is the possible combinations for the given slot
     */
    fun onRecipeTransfer(player: PlayerEntity?, recipe: Array<Array<ItemStack?>?>?)

    /**
     * Called when the grid is closed.
     *
     * @param player the player
     */
    fun onClosed(player: PlayerEntity?)

    /**
     * @return true if the grid is active, false otherwise
     */
    val isGridActive: Boolean

    /**
     * @return the slot id where this grid is located, if applicable, otherwise -1
     */
    val slotId: Int

    companion object {
        @kotlin.jvm.JvmStatic
        fun isValidViewType(type: Int): Boolean {
            return type == VIEW_TYPE_NORMAL || type == VIEW_TYPE_CRAFTABLES || type == VIEW_TYPE_NON_CRAFTABLES
        }

        @kotlin.jvm.JvmStatic
        fun isValidSearchBoxMode(mode: Int): Boolean {
            return mode == SEARCH_BOX_MODE_NORMAL || mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED || mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED
        }

        @kotlin.jvm.JvmStatic
        fun isSearchBoxModeWithAutoselection(mode: Int): Boolean {
            return mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED
        }

        @kotlin.jvm.JvmStatic
        fun isValidSortingType(type: Int): Boolean {
            return type == SORTING_TYPE_QUANTITY || type == SORTING_TYPE_NAME || type == SORTING_TYPE_ID || type == SORTING_TYPE_INVENTORYTWEAKS || type == SORTING_TYPE_LAST_MODIFIED
        }

        @kotlin.jvm.JvmStatic
        fun isValidSortingDirection(direction: Int): Boolean {
            return direction == SORTING_DIRECTION_ASCENDING || direction == SORTING_DIRECTION_DESCENDING
        }

        @kotlin.jvm.JvmStatic
        fun isValidSize(size: Int): Boolean {
            return size == SIZE_STRETCH || size == SIZE_SMALL || size == SIZE_MEDIUM || size == SIZE_LARGE
        }

        const val TABS_PER_PAGE = 5
        const val SORTING_DIRECTION_ASCENDING = 0
        const val SORTING_DIRECTION_DESCENDING = 1
        const val SORTING_TYPE_QUANTITY = 0
        const val SORTING_TYPE_NAME = 1
        const val SORTING_TYPE_ID = 2
        const val SORTING_TYPE_INVENTORYTWEAKS = 3
        const val SORTING_TYPE_LAST_MODIFIED = 4
        const val SEARCH_BOX_MODE_NORMAL = 0
        const val SEARCH_BOX_MODE_NORMAL_AUTOSELECTED = 1
        const val SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 2
        const val SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED = 3
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_NON_CRAFTABLES = 1
        const val VIEW_TYPE_CRAFTABLES = 2
        const val SIZE_STRETCH = 0
        const val SIZE_SMALL = 1
        const val SIZE_MEDIUM = 2
        const val SIZE_LARGE = 3
    }
}