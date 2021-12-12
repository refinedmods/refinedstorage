package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.item.Item;

public class CoreItem extends Item {
    public enum Type {
        CONSTRUCTION,
        DESTRUCTION
    }

    public CoreItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP));
    }
}
