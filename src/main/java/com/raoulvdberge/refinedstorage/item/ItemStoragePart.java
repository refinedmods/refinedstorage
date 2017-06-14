package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemStoragePart extends ItemBase {
    public static final int TYPE_1K = 0;
    public static final int TYPE_4K = 1;
    public static final int TYPE_16K = 2;
    public static final int TYPE_64K = 3;

    public ItemStoragePart() {
        super("storage_part");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!RSUtils.canAddToCreativeTab(this, tab)) {
            return;
        }

        for (int i = 0; i <= 3; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
