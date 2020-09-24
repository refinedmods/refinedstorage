package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

public class ControllerBlockItem extends EnergyBlockItem {
    private final IFormattableTextComponent displayName;

    public ControllerBlockItem(ControllerBlock block, DyeColor color, RegistryObject<? extends Block> blockForTranslation) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == NetworkType.CREATIVE, () -> RS.SERVER_CONFIG.getController().getCapacity());

        IFormattableTextComponent name = new StringTextComponent(blockForTranslation.get().getTranslationKey());
        if (color != BlockUtils.DEFAULT_COLOR) {
            name = new TranslationTextComponent("color.minecraft." + color.getTranslationKey()).appendString(" ").append(name);
        }
        this.displayName = name;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return displayName;
    }
}
