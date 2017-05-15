package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.item.filter.FilterTab;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface IGrid {
    GridType getType();

    @Nullable
    INetwork getNetwork();

    @Nullable
    default IItemGridHandler getItemHandler() {
        return getNetwork() != null ? getNetwork().getItemGridHandler() : null;
    }

    @Nullable
    default IFluidGridHandler getFluidHandler() {
        return getNetwork() != null ? getNetwork().getFluidGridHandler() : null;
    }

    String getGuiTitle();

    int getViewType();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    int getTabSelected();

    int getSize();

    void onViewTypeChanged(int type);

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    void onSizeChanged(int size);

    void onTabSelectionChanged(int tab);

    List<Filter> getFilters();

    List<FilterTab> getTabs();

    ItemHandlerBase getFilter();

    TileDataParameter<Integer> getRedstoneModeConfig();

    InventoryCrafting getCraftingMatrix();

    InventoryCraftResult getCraftingResult();

    void onCraftingMatrixChanged();

    void onCrafted(EntityPlayer player);

    void onCraftedShift(EntityPlayer player);

    void onRecipeTransfer(EntityPlayer player, ItemStack[][] recipe);

    void onClosed(EntityPlayer player);

    boolean isActive();
}
