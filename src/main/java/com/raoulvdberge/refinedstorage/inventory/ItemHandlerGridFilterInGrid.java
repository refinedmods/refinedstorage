package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.GridTab;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.item.ItemGridFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class ItemHandlerGridFilterInGrid extends ItemHandlerBasic {
    private List<GridFilter> filteredItems;
    private List<GridTab> tabs;

    public ItemHandlerGridFilterInGrid(List<GridFilter> filteredItems, List<GridTab> tabs) {
        super(4, new ItemValidatorBasic(RSItems.GRID_FILTER));

        this.filteredItems = filteredItems;
        this.tabs = tabs;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filteredItems.clear();
        tabs.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filter = getStackInSlot(i);

            if (!filter.isEmpty()) {
                int compare = ItemGridFilter.getCompare(filter);
                int mode = ItemGridFilter.getMode(filter);
                boolean modFilter = ItemGridFilter.isModFilter(filter);

                ItemHandlerGridFilter items = new ItemHandlerGridFilter(filter);

                List<GridFilter> filters = new ArrayList<>();

                for (ItemStack item : items.getFilteredItems()) {
                    if (!item.isEmpty()) {
                        filters.add(new GridFilter(item, compare, mode, modFilter));
                    }
                }

                ItemStack icon = ItemGridFilter.getIcon(filter);

                if (icon.isEmpty()) {
                    filteredItems.addAll(filters);
                } else {
                    tabs.add(new GridTab(filters, ItemGridFilter.getName(filter), icon));
                }
            }
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            GuiGrid.markForSorting();
        }
    }
}
