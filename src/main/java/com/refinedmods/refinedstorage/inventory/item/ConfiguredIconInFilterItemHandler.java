package com.refinedmods.refinedstorage.inventory.item;

import com.refinedmods.refinedstorage.item.FilterItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ConfiguredIconInFilterItemHandler extends ItemStackHandler {
    private final ItemStack filterItem;

    public ConfiguredIconInFilterItemHandler(ItemStack filterItem) {
        super(1);

        this.filterItem = filterItem;

        setStackInSlot(0, FilterItem.getIcon(filterItem));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        FilterItem.setIcon(filterItem, getStackInSlot(0));
    }
}
