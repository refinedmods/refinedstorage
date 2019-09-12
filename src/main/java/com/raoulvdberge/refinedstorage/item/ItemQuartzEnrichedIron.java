package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.item.Item;

public class ItemQuartzEnrichedIron extends Item {
    public ItemQuartzEnrichedIron() {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, "quartz_enriched_iron");
    }
}
