package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);
    public static final EnumProperty<DyeColor> COLOR_PROPERTY = EnumProperty.create("color", DyeColor.class);

    public static BlockItem createBlockItemFor(BaseBlock block) {
        BaseBlockItem blockItem = new BaseBlockItem(block, new Item.Properties().group(RS.MAIN_GROUP));

        blockItem.setRegistryName(block.getRegistryName());

        return blockItem;
    }
}
