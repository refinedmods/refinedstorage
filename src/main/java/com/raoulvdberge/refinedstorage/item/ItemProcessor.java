package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemProcessor extends ItemBase {
    public static final int TYPE_PRINTED_BASIC = 0;
    public static final int TYPE_PRINTED_IMPROVED = 1;
    public static final int TYPE_PRINTED_ADVANCED = 2;
    public static final int TYPE_BASIC = 3;
    public static final int TYPE_IMPROVED = 4;
    public static final int TYPE_ADVANCED = 5;
    public static final int TYPE_PRINTED_SILICON = 6;

    public ItemProcessor() {
        super("processor");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!RSUtils.canAddToCreativeTab(this, tab)) {
            return;
        }

        for (int i = 0; i <= 6; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
