package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.GridBlockGridFactory;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GridBlock extends ColoredNetworkBlock {
    private final GridType type;

    public GridBlock(GridType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GridTile(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ColorMap<GridBlock> map;
        switch (type) {
            case FLUID:
                map = RSBlocks.FLUID_GRID;
                break;
            case NORMAL:
                map = RSBlocks.GRID;
                break;
            case CRAFTING:
                map = RSBlocks.CRAFTING_GRID;
                break;
            case PATTERN:
                map = RSBlocks.PATTERN_GRID;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        ActionResultType result = map.changeBlockColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }

        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> API.instance().getGridManager().openGrid(GridBlockGridFactory.ID, (ServerPlayerEntity) player, pos));
        }

        return ActionResultType.SUCCESS;
    }
}
