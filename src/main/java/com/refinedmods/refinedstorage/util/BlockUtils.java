package com.refinedmods.refinedstorage.util;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);
}
