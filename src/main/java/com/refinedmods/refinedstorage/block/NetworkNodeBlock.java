package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
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
import net.neoforged.neoforge.items.IItemHandler;

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
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);

        if (!level.isClientSide) {
            INetworkNode node = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(pos);
            if (node instanceof NetworkNode) {
                ((NetworkNode) node).setRedstonePowered(level.hasNeighborSignal(pos));
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof NetworkNodeBlockEntity) {
                IItemHandler handler = ((NetworkNodeBlockEntity) blockEntity).getNode().getDrops();

                if (handler != null) {
                    NonNullList<ItemStack> drops = NonNullList.create();

                    for (int i = 0; i < handler.getSlots(); ++i) {
                        drops.add(handler.getStackInSlot(i));
                    }

                    Containers.dropContents(level, pos, drops);
                }
            }
        }

        // Call onReplaced after the drops check so the tile still exists
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void onDirectionChanged(Level level, BlockPos pos, Direction newDirection) {
        super.onDirectionChanged(level, pos, newDirection);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof INetworkNodeProxy) {
            INetworkNode node = ((INetworkNodeProxy) blockEntity).getNode();

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
