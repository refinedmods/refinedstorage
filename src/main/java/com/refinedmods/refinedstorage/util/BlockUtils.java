package com.refinedmods.refinedstorage.util;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class BlockUtils {
    public static final AbstractBlock.Properties DEFAULT_ROCK_PROPERTIES = AbstractBlock.Properties.of(Material.STONE).strength(1.9F).sound(SoundType.STONE);
    public static final AbstractBlock.Properties DEFAULT_GLASS_PROPERTIES = AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.35F);

    private BlockUtils() {
    }

    // Block#getTranslatedName is client only
    public static ITextComponent getBlockTranslation(Block block) {
        return new TranslationTextComponent(block.getDescriptionId());
    }
}
