package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.world.item.Item;

public class CoreItem extends Item {
    public CoreItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP));
    }

    public enum Type {
        CONSTRUCTION,
        DESTRUCTION
    }
}
