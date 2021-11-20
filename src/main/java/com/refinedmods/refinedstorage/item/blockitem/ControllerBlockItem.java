package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ControllerBlockItem extends EnergyBlockItem {
    private final ITextComponent displayName;

    public ControllerBlockItem(ControllerBlock block, DyeColor color, ITextComponent displayName) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == NetworkType.CREATIVE, () -> RS.SERVER_CONFIG.getController().getCapacity());

        if (color != ColorMap.DEFAULT_COLOR) {
            this.displayName = new TranslationTextComponent("color.minecraft." + color.getTranslationKey())
                    .appendString(" ")
                    .appendSibling(displayName);
        } else {
            this.displayName = displayName;
        }

    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return displayName;
    }
}
