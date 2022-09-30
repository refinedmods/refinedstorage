package com.refinedmods.refinedstorage.util;

import net.minecraft.network.chat.Component;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public final class BlockUtils {
    public static final BlockBehaviour.Properties DEFAULT_ROCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(0.5F, 6.0F).sound(SoundType.STONE);
    public static final BlockBehaviour.Properties DEFAULT_GLASS_PROPERTIES = BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.35F);

    private BlockUtils() {
    }

    // Block#getTranslatedName is client only
    public static Component getBlockTranslation(Block block) {
        return Component.translatable(block.getDescriptionId());
    }
}
