package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.factory.PortableGridBlockGridFactory;
import com.raoulvdberge.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGridDiskState;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGridTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
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

    private static final VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 13.2, 16);

    private final PortableGridBlockItem.Type type;

    public PortableGridBlock(PortableGridBlockItem.Type type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.setRegistryName(RS.ID, (type == PortableGridBlockItem.Type.CREATIVE ? "creative_" : "") + "portable_grid");
        this.setDefaultState(getDefaultState().with(DISK_STATE, PortableGridDiskState.NONE).with(ACTIVE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            API.instance().getGridManager().openGrid(PortableGridBlockGridFactory.ID, (ServerPlayerEntity) player, pos);

            ((PortableGridTile) world.getTileEntity(pos)).onOpened();
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            ((PortableGridTile) world.getTileEntity(pos)).applyDataFromItemToTile(stack);
            ((PortableGridTile) world.getTileEntity(pos)).updateState();
        }
    }
}
