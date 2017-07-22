package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.GridTab;
import com.raoulvdberge.refinedstorage.apiimpl.util.Filter;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.item.ItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemHandlerFilter extends ItemHandlerBase {
    private List<IFilter> filters;
    private List<IGridTab> tabs;

    public ItemHandlerFilter(List<IFilter> filters, List<IGridTab> tabs, @Nullable Consumer<Integer> listener) {
        super(4, listener, new ItemValidatorBasic(RSItems.FILTER));

        this.filters = filters;
        this.tabs = tabs;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filters.clear();
        tabs.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filter = getStackInSlot(i);

            if (!filter.isEmpty()) {
                int compare = ItemFilter.getCompare(filter);
                int mode = ItemFilter.getMode(filter);
                boolean modFilter = ItemFilter.isModFilter(filter);

                ItemHandlerFilterItems items = new ItemHandlerFilterItems(filter);

                List<IFilter> filters = new ArrayList<>();

                for (ItemStack item : items.getFilteredItems()) {
                    if (!item.isEmpty()) {
                        filters.add(new Filter(item, compare, mode, modFilter));
                    }
                }

                ItemStack icon = ItemFilter.getIcon(filter);

                if (icon.isEmpty()) {
                    this.filters.addAll(filters);
                } else {
                    tabs.add(new GridTab(filters, ItemFilter.getName(filter), icon));
                }
            }
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            GuiGrid.markForSorting();
        }
    }
}
