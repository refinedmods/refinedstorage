package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import net.minecraft.item.Item;

public class ControllerBlockItem extends EnergyBlockItem {
    public ControllerBlockItem(ControllerBlock block) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == ControllerBlock.Type.CREATIVE, () -> RS.CONFIG.getController().getCapacity());

        this.setRegistryName(block.getRegistryName());
    }
}
