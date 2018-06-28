package com.raoulvdberge.refinedstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemProcessor extends ItemBase {
    public static final int TYPE_CUT_BASIC = 0;
    public static final int TYPE_CUT_IMPROVED = 1;
    public static final int TYPE_CUT_ADVANCED = 2;
    public static final int TYPE_BASIC = 3;
    public static final int TYPE_IMPROVED = 4;
    public static final int TYPE_ADVANCED = 5;
    public static final int TYPE_CUT_SILICON = 6;

    public ItemProcessor() {
        super("processor");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 6; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
