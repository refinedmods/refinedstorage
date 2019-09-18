package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);

    public static BlockItem createBlockItemFor(Block block) {
        BlockItem blockItem = new BlockItem(block, new Item.Properties().group(RS.MAIN_GROUP));

        blockItem.setRegistryName(block.getRegistryName());

        return blockItem;
    }
}
