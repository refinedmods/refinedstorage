package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.items.IItemHandler;

public abstract class NetworkNodeBlock extends BaseBlock implements EntityBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    protected NetworkNodeBlock(BlockBehaviour.Properties props) {
        super(props);

        if (hasConnectedState()) {
            this.registerDefaultState(this.getStateDefinition().any().setValue(CONNECTED, false));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);

        if (!world.isClientSide) {
            INetworkNode node = API.instance().getNetworkNodeManager((ServerLevel) world).getNode(pos);
            if (node instanceof NetworkNode) {
                ((NetworkNode) node).setRedstonePowered(world.hasNeighborSignal(pos));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = world.getBlockEntity(pos);

            if (tile instanceof NetworkNodeTile) {
                IItemHandler handler = ((NetworkNodeTile) tile).getNode().getDrops();

                if (handler != null) {
                    NonNullList<ItemStack> drops = NonNullList.create();

                    for (int i = 0; i < handler.getSlots(); ++i) {
                        drops.add(handler.getStackInSlot(i));
                    }

                    Containers.dropContents(world, pos, drops);
                }
            }
        }

        // Call onReplaced after the drops check so the tile still exists
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    protected void onDirectionChanged(Level world, BlockPos pos, Direction newDirection) {
        super.onDirectionChanged(world, pos, newDirection);

        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof INetworkNodeProxy) {
            INetworkNode node = ((INetworkNodeProxy) tile).getNode();

            if (node instanceof NetworkNode) {
                ((NetworkNode) node).onDirectionChanged(newDirection);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        if (hasConnectedState()) {
            builder.add(CONNECTED);
        }
    }

    public boolean hasConnectedState() {
        return false;
    }
}
