package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.world.item.Item;

public class ProcessorItem extends Item {
    public ProcessorItem() {
        super(new Item.Properties().tab(RS.CREATIVE_MODE_TAB));
    }

    public enum Type {
        RAW_BASIC("raw_basic"),
        RAW_IMPROVED("raw_improved"),
        RAW_ADVANCED("raw_advanced"),
        BASIC("basic"),
        IMPROVED("improved"),
        ADVANCED("advanced");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
