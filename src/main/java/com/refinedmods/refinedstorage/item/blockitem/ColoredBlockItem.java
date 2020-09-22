package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;


public class ColoredBlockItem extends BaseBlockItem {
    private final DyeColor color;
    private final RegistryObject<? extends Block> blockForTranslation;

    public ColoredBlockItem(BaseBlock block, Properties builder, DyeColor color, RegistryObject<? extends Block> blockForTranslation) {
        super(block, builder);
        this.color = color;
        this.blockForTranslation = blockForTranslation;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IFormattableTextComponent text = new StringTextComponent("");
        if (color != DyeColor.LIGHT_BLUE) {
            text.append(new TranslationTextComponent("color.minecraft." + color.getTranslationKey()));
            text.append(new StringTextComponent(" "));
        }

        text.append(new TranslationTextComponent(blockForTranslation.get().getTranslationKey()));
        return text;
    }
}
