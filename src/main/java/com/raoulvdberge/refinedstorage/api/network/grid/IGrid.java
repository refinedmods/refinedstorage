package com.raoulvdberge.refinedstorage.api.network.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

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

    List<IFilter> getFilters();

    List<IGridTab> getTabs();

    IItemHandlerModifiable getFilter();

    InventoryCrafting getCraftingMatrix();

    InventoryCraftResult getCraftingResult();

    void onCraftingMatrixChanged();

    void onCrafted(EntityPlayer player);

    void onCraftedShift(EntityPlayer player);

    void onRecipeTransfer(EntityPlayer player, ItemStack[][] recipe);

    void onClosed(EntityPlayer player);

    boolean isActive();
}
