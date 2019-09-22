package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

public abstract class NodeBlock extends Block {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public NodeBlock(Block.Properties props) {
        super(props);
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(world, pos, state);

        if (!world.isRemote()) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) world);

            INetworkNode node = manager.getNode(pos);

            manager.removeNode(pos);
            manager.markForSaving();

            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().world(), node.getNetwork().getPosition());
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        if (hasConnectedState()) {
            builder.add(CONNECTED);
        }
    }

    public boolean hasConnectedState() {
        return false;
    }
}
