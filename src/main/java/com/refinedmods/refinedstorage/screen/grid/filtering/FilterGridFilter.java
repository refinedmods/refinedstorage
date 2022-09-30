package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Predicate;

public class FilterGridFilter implements Predicate<IGridStack> {
    private final List<IFilter> filters;

    public FilterGridFilter(List<IFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean test(IGridStack stack) {
        if (filters.isEmpty()) {
            return true;
        }

        int lastMode = IFilter.MODE_WHITELIST;

        for (IFilter filter : filters) {
            lastMode = filter.getMode();

            if (stack instanceof ItemGridStack && filter.getStack() instanceof ItemStack) {
                ItemStack stackInFilter = (ItemStack) filter.getStack();

                if (filter.isModFilter()) {
                    String stackModId = stack.getModId();
                    String filterModId = stackInFilter.getItem().getCreatorModId(stackInFilter);

                    if (filterModId != null && filterModId.equals(stackModId)) {
                        return filter.getMode() == IFilter.MODE_WHITELIST;
                    }
                } else if (API.instance().getComparer().isEqual(((ItemGridStack) stack).getStack(), stackInFilter, filter.getCompare())) {
                    return filter.getMode() == IFilter.MODE_WHITELIST;
                }
            } else if (stack instanceof FluidGridStack && filter.getStack() instanceof FluidStack) {
                FluidStack stackInFilter = (FluidStack) filter.getStack();

                if (filter.isModFilter()) {
                    ResourceLocation stackInFilterRegistryName = ForgeRegistries.FLUIDS.getKey(stackInFilter.getFluid());

                    if (stackInFilterRegistryName != null) {
                        String stackInFilterModId = stackInFilterRegistryName.getNamespace();

                        if (stackInFilterModId.equalsIgnoreCase(stack.getModId())) {
                            return filter.getMode() == IFilter.MODE_WHITELIST;
                        }
                    }
                } else if (API.instance().getComparer().isEqual(((FluidGridStack) stack).getStack(), stackInFilter, filter.getCompare())) {
                    return filter.getMode() == IFilter.MODE_WHITELIST;
                }
            }
        }

        return lastMode != IFilter.MODE_WHITELIST;
    }
}
