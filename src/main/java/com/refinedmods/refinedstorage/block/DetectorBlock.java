package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.container.DetectorContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.DetectorTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DetectorBlock extends ColoredNetworkBlock {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 5, 16);

    public DetectorBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tile = world.getBlockEntity(pos);

        return (tile instanceof DetectorTile && ((DetectorTile) tile).getNode().isPowered()) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ColorMap<DetectorBlock> colorMap = RSBlocks.DETECTOR;
        DyeColor color = DyeColor.getColor(player.getItemInHand(hand));

        if (color != null && !state.getBlock().equals(colorMap.get(color).get())) {
            BlockState newState = colorMap.get(color).get().defaultBlockState().setValue(POWERED, state.getValue(POWERED));

            return RSBlocks.DETECTOR.setBlockState(newState, player.getItemInHand(hand), world, pos, player);
        }

        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<DetectorTile>(
                    new TranslationTextComponent("gui.refinedstorage.detector"),
                    (tile, windowId, inventory, p) -> new DetectorContainer(tile, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DetectorTile();
    }
}
