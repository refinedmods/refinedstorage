package com.raoulvdberge.refinedstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemFluidStoragePart extends ItemBase {
    public static final int TYPE_64K = 0;
    public static final int TYPE_256K = 1;
    public static final int TYPE_1024K = 2;
    public static final int TYPE_4096K = 3;

    public ItemFluidStoragePart() {
        super("fluid_storage_part");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 3; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
