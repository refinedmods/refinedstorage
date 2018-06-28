package com.raoulvdberge.refinedstorage.item;

import net.minecraft.item.ItemStack;

public class ItemCuttingTool extends ItemBase {
    public ItemCuttingTool() {
        super("cutting_tool");

        setMaxDamage(50 - 1);
        setMaxStackSize(1);
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.setItemDamage(stack.getItemDamage() + 1);

        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }
}
