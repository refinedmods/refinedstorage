package com.refinedmods.refinedstorage.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);
    public static final EnumProperty<DyeColor> COLOR_PROPERTY = EnumProperty.create("color", DyeColor.class);
    public static final List<Item> COLORED_BLOCK_ITEMS = new ArrayList<>();

    public static ActionResultType changeBlockColor(BlockState state, ItemStack heldItem, World worldIn, BlockPos pos, PlayerEntity player) {
        if (heldItem.getItem() instanceof DyeItem) {
            DyeColor color = DyeColor.getColor(heldItem);
            if (color == null) {
                return ActionResultType.PASS;
            }
            if (state.get(COLOR_PROPERTY) == color) {
                return ActionResultType.PASS;
            } else {
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos, state.with(COLOR_PROPERTY, color));
                    if (((ServerPlayerEntity) player).interactionManager.getGameType() != GameType.CREATIVE) {
                        heldItem.shrink(1);
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
