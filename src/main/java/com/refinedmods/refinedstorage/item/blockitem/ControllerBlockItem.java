package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.item.Item;

public class ControllerBlockItem extends EnergyBlockItem {
    public ControllerBlockItem(ControllerBlock block) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == NetworkType.CREATIVE, () -> RS.SERVER_CONFIG.getController().getCapacity());

        BlockUtils.COLORED_BLOCK_ITEMS.add(this);
    }
}
