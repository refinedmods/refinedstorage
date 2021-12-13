package com.refinedmods.refinedstorage.apiimpl.network.grid.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class GridBlockGridFactory implements IGridFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid");

    @Override
    @Nullable
    public IGrid createFromStack(Player player, ItemStack stack, PlayerSlot slot) {
        return null;
    }

    @Override
    @Nullable
    public IGrid createFromBlock(Player player, BlockPos pos) {
        BlockEntity blockEntity = getRelevantBlockEntity(player.getCommandSenderWorld(), pos);

        if (blockEntity instanceof GridBlockEntity) {
            return ((GridBlockEntity) blockEntity).getNode();
        }

        return null;
    }

    @Nullable
    @Override
    public BlockEntity getRelevantBlockEntity(Level level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    @Override
    public GridFactoryType getType() {
        return GridFactoryType.BLOCK;
    }
}
