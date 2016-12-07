package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.item.ItemGridFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class ItemHandlerGridFilterInGrid extends ItemHandlerBasic {
    private List<GridFilteredItem> filteredItems;

    public ItemHandlerGridFilterInGrid(List<GridFilteredItem> filteredItems) {
        super(4, new ItemValidatorBasic(RSItems.GRID_FILTER));

        this.filteredItems = filteredItems;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filteredItems.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filter = getStackInSlot(i);

            if (!filter.isEmpty()) {
                int compare = ItemGridFilter.getCompare(filter);
                int mode = ItemGridFilter.getMode(filter);

                ItemHandlerGridFilter items = new ItemHandlerGridFilter(filter);

                for (ItemStack item : items.getFilteredItems()) {
                    if (!item.isEmpty()) {
                        filteredItems.add(new GridFilteredItem(item, compare, mode));
                    }
                }
            }
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            GuiGrid.markForSorting();
        }
    }
}
