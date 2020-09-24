package com.refinedmods.refinedstorage.util;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);
    public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_BLUE;

}
