package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.GridTab;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface IGrid {
    EnumGridType getType();

    @Nullable
    INetworkMaster getNetwork();

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

    List<GridFilter> getFilteredItems();

    List<GridTab> getTabs();

    ItemHandlerBasic getFilter();

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
