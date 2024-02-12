package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a grid.
 */
public interface IGrid {
    int TABS_PER_PAGE = 5;

    int SORTING_DIRECTION_ASCENDING = 0;
    int SORTING_DIRECTION_DESCENDING = 1;

    int SORTING_TYPE_QUANTITY = 0;
    int SORTING_TYPE_NAME = 1;
    int SORTING_TYPE_ID = 2;
    int SORTING_TYPE_LAST_MODIFIED = 4;

    int SEARCH_BOX_MODE_NORMAL = 0;
    int SEARCH_BOX_MODE_NORMAL_AUTOSELECTED = 1;
    int SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 2;
    int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED = 3;
    int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY = 4;
    int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED = 5;

    int VIEW_TYPE_NORMAL = 0;
    int VIEW_TYPE_NON_CRAFTABLES = 1;
    int VIEW_TYPE_CRAFTABLES = 2;

    int SIZE_STRETCH = 0;
    int SIZE_SMALL = 1;
    int SIZE_MEDIUM = 2;
    int SIZE_LARGE = 3;

    static boolean isValidViewType(int type) {
        return type == VIEW_TYPE_NORMAL ||
                type == VIEW_TYPE_CRAFTABLES ||
                type == VIEW_TYPE_NON_CRAFTABLES;
    }

    static boolean isValidSearchBoxMode(int mode) {
        return mode == SEARCH_BOX_MODE_NORMAL ||
                mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED;
    }

    static boolean isSearchBoxModeWithAutoselection(int mode) {
        return mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED;
    }

    static boolean doesSearchBoxModeUseJEI(int mode) {
        return mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED;
    }

    static boolean isValidSortingType(int type) {
        return type == SORTING_TYPE_QUANTITY ||
                type == SORTING_TYPE_NAME ||
                type == SORTING_TYPE_ID ||
                type == SORTING_TYPE_LAST_MODIFIED;
    }

    static boolean isValidSortingDirection(int direction) {
        return direction == SORTING_DIRECTION_ASCENDING || direction == SORTING_DIRECTION_DESCENDING;
    }

    static boolean isValidSize(int size) {
        return size == SIZE_STRETCH ||
                size == SIZE_SMALL ||
                size == SIZE_MEDIUM ||
                size == SIZE_LARGE;
    }

    /**
     * @return the grid type
     */
    GridType getGridType();

    /**
     * @param player the player to create a listener for
     * @return a listener for this grid, will be attached to the storage cache in {@link #getStorageCache()}
     */
    IStorageCacheListener createListener(ServerPlayer player);

    /**
     * @return the storage cache for this grid, or null if this grid is unavailable
     */
    @Nullable
    IStorageCache getStorageCache();

    /**
     * @return the item grid handler, or null if there is no handler available
     */
    @Nullable
    IItemGridHandler getItemHandler();

    /**
     * @return the fluid grid handler, or null if there is no handler available
     */
    @Nullable
    IFluidGridHandler getFluidHandler();

    /**
     * @param listener the listener
     */
    default void addCraftingListener(ICraftingGridListener listener) {
    }

    /**
     * @param listener the listener
     */
    default void removeCraftingListener(ICraftingGridListener listener) {
    }

    /**
     * @return the title
     */
    Component getTitle();

    /**
     * @return the view type
     */
    int getViewType();

    /**
     * @return the sorting type
     */
    int getSortingType();

    /**
     * @return the sorting direction
     */
    int getSortingDirection();

    /**
     * @return the search box mode
     */
    int getSearchBoxMode();

    /**
     * @return the current tab that is selected
     */
    int getTabSelected();

    /**
     * @return the current page that the tab is on
     */
    int getTabPage();

    /**
     * @return the total amount of tab pages
     */
    int getTotalTabPages();

    /**
     * @return the size mode
     */
    int getSize();

    /**
     * @param type the new view type
     */
    void onViewTypeChanged(int type);

    /**
     * @param type the new sorting type
     */
    void onSortingTypeChanged(int type);

    /**
     * @param direction the new direction
     */
    void onSortingDirectionChanged(int direction);

    /**
     * @param searchBoxMode the new search box mode
     */
    void onSearchBoxModeChanged(int searchBoxMode);

    /**
     * @param size the new size mode
     */
    void onSizeChanged(int size);

    /**
     * @param tab the new selected tab
     */
    void onTabSelectionChanged(int tab);

    /**
     * @param page the new selected page
     */
    void onTabPageChanged(int page);

    /**
     * @return the filters
     */
    List<IFilter> getFilters();

    /**
     * @return the tabs
     */
    List<IGridTab> getTabs();

    /**
     * @return the inventory of the filters
     */
    IItemHandlerModifiable getFilter();

    /**
     * @return the crafting matrix, or null if not a crafting grid
     */
    @Nullable
    CraftingContainer getCraftingMatrix();

    /**
     * @return the crafting result inventory, or null if not a crafting grid
     */
    @Nullable
    ResultContainer getCraftingResult();

    /**
     * Called when the crafting matrix changes.
     */
    void onCraftingMatrixChanged();

    /**
     * Called when an item is crafted in a crafting grid.
     *
     * @param player         the player that crafted the item
     * @param availableItems the items available for shift crafting
     * @param usedItems      the items used by shift crafting
     */
    void onCrafted(Player player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems);

    /**
     * Called when the clear button is pressed in the pattern grid or crafting grid.
     */
    void onClear(Player player);

    /**
     * Called when an item is crafted with shift click (up to 64 items) in a crafting grid.
     *
     * @param player the player that crafted the item
     */
    void onCraftedShift(Player player);

    /**
     * Called when a JEI recipe transfer occurs.
     *
     * @param player the player
     * @param recipe a 9*x array stack array, where x is the possible combinations for the given slot
     */
    void onRecipeTransfer(Player player, ItemStack[][] recipe);

    /**
     * Called when the grid is closed.
     *
     * @param player the player
     */
    void onClosed(Player player);

    /**
     * @return true if the grid is active, false otherwise
     */
    boolean isGridActive();

    /**
     * @return the slot id where this grid is located, if applicable, otherwise -1
     */
    int getSlotId();
}
