package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CuttingToolItem extends Item {
    public CuttingToolItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxDamage(50 - 1));

        this.setRegistryName(RS.ID, "cutting_tool");
    }

    @Override
    public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.setDamage(stack.getDamage() + 1);

        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }
}
