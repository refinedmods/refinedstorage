package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public abstract class NodeBlock extends BaseBlock {
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
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = worldIn.getTileEntity(pos);

            if (tile instanceof NetworkNodeTile) {
                IItemHandler handler = ((NetworkNodeTile) tile).getNode().getDrops();

                if (handler != null) {
                    NonNullList<ItemStack> drops = NonNullList.create();

                    for (int i = 0; i < handler.getSlots(); ++i) {
                        drops.add(handler.getStackInSlot(i));
                    }

                    InventoryHelper.dropItems(worldIn, pos, drops);
                }
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        if (hasConnectedState()) {
            builder.add(CONNECTED);
        }
    }

    /* TODO - Covers needed for this one
    @Override
    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
        if (!world.isRemote && getDirection() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            Direction newDirection = getDirection().cycle(tile.getDirection());

            if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable && ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().hasCover(newDirection)) {
                return false;
            }
        }

        return super.rotateBlock(world, pos, axis);
    }*/

    public boolean hasConnectedState() {
        return false;
    }
}
