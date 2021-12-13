package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.inventory.fluid.ConfiguredFluidsInFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.fluid.ConfiguredIconInFluidFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.ConfiguredIconInFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ConfiguredItemsInFilterItemHandler;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FilterContainer extends BaseContainer {
    private final ItemStack filterItem;

    public FilterContainer(Player player, ItemStack filterItem, int windowId) {
        super(RSContainers.FILTER, null, player, windowId);

        this.filterItem = filterItem;

        int y = 20;
        int x = 8;

        ConfiguredItemsInFilterItemHandler filter = new ConfiguredItemsInFilterItemHandler(filterItem);
        FluidInventory fluidFilter = new ConfiguredFluidsInFilterItemHandler(filterItem);

        for (int i = 0; i < 27; ++i) {
            addSlot(new FilterSlot(filter, i, x, y).setEnableHandler(() -> FilterItem.getType(filterItem) == IType.ITEMS));
            addSlot(new FluidFilterSlot(fluidFilter, i, x, y).setEnableHandler(() -> FilterItem.getType(filterItem) == IType.FLUIDS));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlot(new FilterSlot(new ConfiguredIconInFilterItemHandler(filterItem), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(filterItem) == IType.ITEMS));
        addSlot(new FluidFilterSlot(new ConfiguredIconInFluidFilterItemHandler(filterItem), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(filterItem) == IType.FLUIDS));

        addPlayerInventory(8, 149);

        transferManager.addFilterTransfer(player.getInventory(), filter, fluidFilter, () -> FilterItem.getType(filterItem));
    }

    public ItemStack getFilterItem() {
        return filterItem;
    }

    @Override
    protected int getDisabledSlotNumber() {
        return getPlayer().getInventory().selected;
    }
}
