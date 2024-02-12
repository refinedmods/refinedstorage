package com.refinedmods.refinedstorage.inventory.item;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.apiimpl.network.grid.GridTab;
import com.refinedmods.refinedstorage.apiimpl.util.FluidFilter;
import com.refinedmods.refinedstorage.apiimpl.util.ItemFilter;
import com.refinedmods.refinedstorage.inventory.fluid.ConfiguredFluidsInFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;

public class FilterItemHandler extends BaseItemHandler {
    private final List<IFilter> filters;
    private final List<IGridTab> tabs;

    public FilterItemHandler(List<IFilter> filters, List<IGridTab> tabs) {
        super(4);

        this.filters = filters;
        this.tabs = tabs;

        this.addValidator(new ItemValidator(RSItems.FILTER.get()));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filters.clear();
        tabs.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filterItem = getStackInSlot(i);
            if (!filterItem.isEmpty()) {
                handleFilterItem(filterItem);
            }
        }

        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    private void handleFilterItem(ItemStack filterItem) {
        ItemStack icon = FilterItem.getIcon(filterItem);
        FluidStack fluidIcon = FilterItem.getFluidIcon(filterItem);
        int compare = FilterItem.getCompare(filterItem);
        int mode = FilterItem.getMode(filterItem);
        boolean modFilter = FilterItem.isModFilter(filterItem);

        List<IFilter> foundFilters = new ArrayList<>();

        for (ItemStack stack : new ConfiguredItemsInFilterItemHandler(filterItem).getConfiguredItems()) {
            if (stack.getItem() == RSItems.FILTER.get()) {
                handleFilterItem(stack);
            } else if (!stack.isEmpty()) {
                foundFilters.add(new ItemFilter(stack, compare, mode, modFilter));
            }
        }

        for (FluidStack stack : new ConfiguredFluidsInFilterItemHandler(filterItem).getConfiguredFluids()) {
            foundFilters.add(new FluidFilter(stack, compare, mode, modFilter));
        }

        if (icon.isEmpty() && fluidIcon.isEmpty()) {
            filters.addAll(foundFilters);
        } else {
            tabs.add(new GridTab(foundFilters, FilterItem.getFilterName(filterItem), icon, fluidIcon));
        }
    }
}
