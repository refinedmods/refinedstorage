package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import net.minecraft.state.BooleanProperty;

public abstract class BlockNode extends BlockNodeProxy {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BlockNode(IBlockInfo info) {
        super(info);
    }
/* TODO - Remove this class.
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileNode && placer instanceof PlayerEntity) {
                ((TileNode) tile).getNode().setOwner(((PlayerEntity) placer).getGameProfile().getId());
            }

            API.instance().discoverNode(world, pos);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(pos);

        dropContents(world, pos);

        removeTile(world, pos, state);

        manager.removeNode(pos);
        manager.markForSaving();

        if (node != null && node.getNetwork() != null) {
            node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().world(), node.getNetwork().getPosition());
        }
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        BlockStateContainer.Builder builder = super.createBlockStateBuilder();

        if (hasConnectedState()) {
            builder.add(CONNECTED);
        }

        return builder;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder().build();
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);

        if (hasConnectedState()) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileNode) {
                return state.withProperty(CONNECTED, ((TileNode) tile).getNode().isActive());
            }
        }

        return state;
    }*/

    public boolean hasConnectedState() {
        return false;
    }
}
