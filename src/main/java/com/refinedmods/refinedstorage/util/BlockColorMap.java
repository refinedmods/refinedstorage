package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockColorMap<T extends Block> extends ColorMap<Block, T> {
    public BlockColorMap(DeferredRegister<Block> registry) {
        super(registry);
    }

    public BlockColorMap(DeferredRegister<Block> registry, List<Runnable> lateRegistration) {
        super(registry, lateRegistration);
    }

    public Block[] getBlocks() {
        return map.values().stream().map(DeferredHolder::get).toArray(Block[]::new);
    }

    public void registerBlocks(String name, Supplier<T> blockFactory) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color != DEFAULT_COLOR ? color + "_" : "";
            DeferredHolder<Block, T> block = registry.register(prefix + name, blockFactory);
            map.put(color, block);
            RSBlocks.COLORED_BLOCKS.add(block);
        }
        RSBlocks.COLORED_BLOCK_TAGS.put(
            BlockTags.create(new ResourceLocation(RS.ID, get(DEFAULT_COLOR).getId().getPath())),
            this
        );
    }

    public <S extends BaseBlock> InteractionResult changeBlockColor(BlockState state, ItemStack heldItem, Level level,
                                                                    BlockPos pos, Player player) {
        DyeColor color = DyeColor.getColor(heldItem);
        if (color == null || state.getBlock().equals(map.get(color).get())) {
            return InteractionResult.PASS;
        }

        return setBlockState(getNewState((DeferredHolder<Block, S>) map.get(color), state), heldItem, level, pos,
            player);
    }

    private <S extends BaseBlock> BlockState getNewState(DeferredHolder<Block, S> block, BlockState state) {
        BlockState newState = block.get().defaultBlockState();

        if (((NetworkNodeBlock) block.get()).hasConnectedState()) {
            newState = newState.setValue(NetworkNodeBlock.CONNECTED, state.getValue(NetworkNodeBlock.CONNECTED));
        }
        if (block.get().getDirection() != BlockDirection.NONE) {
            newState = newState.setValue(block.get().getDirection().getProperty(),
                state.getValue(block.get().getDirection().getProperty()));
        }

        return newState;
    }

    public InteractionResult setBlockState(BlockState newState, ItemStack heldItem, Level level, BlockPos pos,
                                           Player player) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, newState);
            if (((ServerPlayer) player).gameMode.getGameModeForPlayer() != GameType.CREATIVE) {
                heldItem.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
