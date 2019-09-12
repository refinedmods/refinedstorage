package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.item.info.IItemInfo;
import net.minecraft.item.Item;

public abstract class ItemBase extends Item {
    protected IItemInfo info;

    public ItemBase(Item.Properties props) {
        super(props);
    }

    public ItemBase(IItemInfo info) {
        super(new Item.Properties());
        this.info = info;

        setRegistryName(info.getId());
        // TODO setCreativeTab(RS.INSTANCE.tab);
    }
}
