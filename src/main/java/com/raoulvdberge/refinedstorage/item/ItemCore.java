package com.raoulvdberge.refinedstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCore extends ItemBase {
    public static final int TYPE_CONSTRUCTION = 0;
    public static final int TYPE_DESTRUCTION = 1;

    public ItemCore() {
        super("core");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i < 2; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
