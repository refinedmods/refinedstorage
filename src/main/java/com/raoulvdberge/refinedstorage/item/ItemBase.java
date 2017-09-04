package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemBase extends Item {
    private final String name;

    public ItemBase(String name) {
        this.name = name;

        setRegistryName(getDomain(), name);
        setCreativeTab(RS.INSTANCE.tab);
    }

    protected String getDomain() {
        return RS.ID;
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + getDomain() + ":" + name;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + stack.getItemDamage();
        }

        return getUnlocalizedName();
    }
}
