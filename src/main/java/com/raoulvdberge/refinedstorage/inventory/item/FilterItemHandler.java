package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.GridTab;
import com.raoulvdberge.refinedstorage.apiimpl.util.FluidFilter;
import com.raoulvdberge.refinedstorage.apiimpl.util.ItemFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.item.validator.ItemValidator;
import com.raoulvdberge.refinedstorage.item.FilterItem;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

public class FilterItemHandler extends BaseItemHandler {
    private List<IFilter> filters;
    private List<IGridTab> tabs;

    public FilterItemHandler(List<IFilter> filters, List<IGridTab> tabs) {
        super(4);

        this.filters = filters;
        this.tabs = tabs;

        this.addValidator(new ItemValidator(RSItems.FILTER));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filters.clear();
        tabs.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filter = getStackInSlot(i);

            if (!filter.isEmpty()) {
                addFilter(filter);
            }
        }

        if (EffectiveSide.get() == LogicalSide.CLIENT) { // TODO check
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    private void addFilter(ItemStack filter) {
        int compare = FilterItem.getCompare(filter);
        int mode = FilterItem.getMode(filter);
        boolean modFilter = FilterItem.isModFilter(filter);

        List<IFilter> filters = new ArrayList<>();

        FilterItemsItemHandler items = new FilterItemsItemHandler(filter);

        for (ItemStack stack : items.getFilteredItems()) {
            if (stack.getItem() == RSItems.FILTER) {
                addFilter(stack);
            } else if (!stack.isEmpty()) {
                filters.add(new ItemFilter(stack, compare, mode, modFilter));
            }
        }

        FluidInventoryFilter fluids = new FluidInventoryFilter(filter);

        for (FluidStack stack : fluids.getFilteredFluids()) {
            filters.add(new FluidFilter(stack, compare, mode, modFilter));
        }

        ItemStack icon = FilterItem.getIcon(filter);
        FluidStack fluidIcon = FilterItem.getFluidIcon(filter);

        if (icon.isEmpty() && fluidIcon == null) {
            this.filters.addAll(filters);
        } else {
            tabs.add(new GridTab(filters, FilterItem.getName(filter), icon, fluidIcon));
        }
    }
}
