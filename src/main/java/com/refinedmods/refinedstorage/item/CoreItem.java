package com.refinedmods.refinedstorage.item;

import net.minecraft.world.item.Item;

public class CoreItem extends Item {
    public CoreItem() {
        super(new Item.Properties());
    }

    public enum Type {
        CONSTRUCTION,
        DESTRUCTION
    }
}
