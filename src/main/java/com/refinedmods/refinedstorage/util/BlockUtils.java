package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import java.util.Map;

public class BlockUtils {
    public static final Block.Properties DEFAULT_ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.9F).sound(SoundType.STONE);
    public static final Block.Properties DEFAULT_GLASS_PROPERTIES = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F);

    public static <T extends BaseBlock> ActionResultType changeBlockColor(BlockState newState, ItemStack heldItem, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isRemote) {
            world.setBlockState(pos, newState);
            if (((ServerPlayerEntity) player).interactionManager.getGameType() != GameType.CREATIVE) {
                heldItem.shrink(1);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public static <T extends BaseBlock> ActionResultType changeBlockColor(Map<DyeColor, RegistryObject<T>> map, BlockState state, ItemStack heldItem, World world, BlockPos pos, PlayerEntity player) {
        DyeColor color = DyeColor.getColor(heldItem);
        if (color == null) {
            return ActionResultType.PASS;
        }
        return changeBlockColor(getNewState(map.get(color), state), heldItem, world, pos, player);
    }

    private static <T extends BaseBlock> BlockState getNewState(RegistryObject<T> block, BlockState state) {
        return block.get().getDefaultState()
            .with(NetworkNodeBlock.CONNECTED, state.get(NetworkNodeBlock.CONNECTED))
            .with(block.get().getDirection().getProperty(), state.get(block.get().getDirection().getProperty()));
    }
}
