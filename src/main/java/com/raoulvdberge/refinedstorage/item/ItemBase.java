package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemBase extends Item {
    private String name;

    public ItemBase(String name) {
        this.name = name;

        setRegistryName(RS.ID, name);
        setCreativeTab(RS.INSTANCE.tab);
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + RS.ID + ":" + name;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + stack.getItemDamage();
        }

        return getUnlocalizedName();
    }
}
