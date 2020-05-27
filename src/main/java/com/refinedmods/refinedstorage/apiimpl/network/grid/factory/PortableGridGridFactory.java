package com.refinedmods.refinedstorage.apiimpl.network.grid.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PortableGridGridFactory implements IGridFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "portable_grid_item");

    @Nullable
    @Override
    public IGrid createFromStack(PlayerEntity player, ItemStack stack, int slotId) {
        PortableGrid portableGrid = new PortableGrid(player, stack, slotId);

        portableGrid.onOpen();

        return portableGrid;
    }

    @Nullable
    @Override
    public IGrid createFromBlock(PlayerEntity player, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public TileEntity getRelevantTile(World world, BlockPos pos) {
        return null;
    }

    @Override
    public GridFactoryType getType() {
        return GridFactoryType.STACK;
    }
}
