package com.refinedmods.refinedstorage.apiimpl.network.grid.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GridBlockGridFactory implements IGridFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid");

    @Override
    @Nullable
    public IGrid createFromStack(PlayerEntity player, ItemStack stack, PlayerSlot slot) {
        return null;
    }

    @Override
    @Nullable
    public IGrid createFromBlock(PlayerEntity player, BlockPos pos) {
        TileEntity tile = getRelevantTile(player.getCommandSenderWorld(), pos);

        if (tile instanceof GridTile) {
            return ((GridTile) tile).getNode();
        }

        return null;
    }

    @Nullable
    @Override
    public TileEntity getRelevantTile(World world, BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    @Override
    public GridFactoryType getType() {
        return GridFactoryType.BLOCK;
    }
}
