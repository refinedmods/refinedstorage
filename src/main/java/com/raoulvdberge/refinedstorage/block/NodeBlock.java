package com.raoulvdberge.refinedstorage.block;

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
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public abstract class NodeBlock extends BaseBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public NodeBlock(Block.Properties props) {
        super(props);

        if (hasConnectedState()) {
            this.setDefaultState(this.getStateContainer().getBaseState().with(CONNECTED, false));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
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

    public boolean hasConnectedState() {
        return false;
    }
}
