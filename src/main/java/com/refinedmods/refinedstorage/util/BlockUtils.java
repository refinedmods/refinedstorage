package com.refinedmods.refinedstorage.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public final class BlockUtils {
    public static final BlockBehaviour.Properties DEFAULT_ROCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.9F, 6.0F).sound(SoundType.STONE);
    public static final BlockBehaviour.Properties DEFAULT_GLASS_PROPERTIES = BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(0.35F).sound(SoundType.GLASS);

    private BlockUtils() {
    }

    // Block#getTranslatedName is client only
    public static Component getBlockTranslation(Block block) {
        return new TranslatableComponent(block.getDescriptionId());
    }
}
