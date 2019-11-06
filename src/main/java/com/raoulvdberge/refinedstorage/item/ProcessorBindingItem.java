package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.item.Item;

public class ProcessorBindingItem extends Item {
    public ProcessorBindingItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, "processor_binding");
    }
}
