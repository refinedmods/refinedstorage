package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.BaseBlock;
import com.raoulvdberge.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);

    public static BlockItem createBlockItemFor(BaseBlock block) {
        BaseBlockItem blockItem = new BaseBlockItem(block, new Item.Properties().group(RS.MAIN_GROUP));

        blockItem.setRegistryName(block.getRegistryName());

        return blockItem;
    }
}
