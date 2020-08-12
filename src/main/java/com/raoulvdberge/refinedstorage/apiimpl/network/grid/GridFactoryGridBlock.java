package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.GridFactoryType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridFactory;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GridFactoryGridBlock implements IGridFactory {
    @Override
    @Nullable
    public IGrid createFromStack(EntityPlayer player, ItemStack stack, int slotId) {
        return null;
    }

    @Override
    @Nullable
    public IGrid createFromBlock(EntityPlayer player, BlockPos pos) {
        TileEntity tile = getRelevantTile(player.world, pos);

        if (tile instanceof TileGrid) {
            return ((TileGrid) tile).getNode();
        }

        return null;
    }

    @Nullable
    @Override
    public TileEntity getRelevantTile(World world, BlockPos pos) {
        return world.getTileEntity(pos);
    }

    @Override
    public GridFactoryType getType() {
        return GridFactoryType.BLOCK;
    }
}
