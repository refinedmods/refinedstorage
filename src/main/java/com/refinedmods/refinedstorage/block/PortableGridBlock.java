package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridBlockGridFactory;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PortableGridBlock extends BaseBlock {
    public static final EnumProperty<PortableGridDiskState> DISK_STATE = EnumProperty.create("disk_state", PortableGridDiskState.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 13.2, 16);

    private final PortableGridBlockItem.Type type;

    public PortableGridBlock(PortableGridBlockItem.Type type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.registerDefaultState(defaultBlockState().setValue(DISK_STATE, PortableGridDiskState.NONE).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(DISK_STATE);
        builder.add(ACTIVE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PortableGridTile(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridBlockGridFactory.ID, (ServerPlayerEntity) player, pos);

            ((PortableGridTile) world.getBlockEntity(pos)).onOpened();
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (!world.isClientSide) {
            ((PortableGridTile) world.getBlockEntity(pos)).applyDataFromItemToTile(stack);
            ((PortableGridTile) world.getBlockEntity(pos)).updateState();
        }
    }
}
