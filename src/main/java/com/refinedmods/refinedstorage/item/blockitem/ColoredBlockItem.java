package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

public class ColoredBlockItem extends BaseBlockItem {
    private final IFormattableTextComponent displayName;

    public ColoredBlockItem(BaseBlock block, Properties builder, DyeColor color, RegistryObject<? extends Block> blockForTranslation) {
        super(block, builder);

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
