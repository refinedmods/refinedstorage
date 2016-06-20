package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.tile.TileSlave;

public abstract class BlockSlave extends BlockBase {
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public BlockSlave(String name) {
        super(name);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION,
            CONNECTED
        });
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(CONNECTED, ((TileSlave) world.getTileEntity(pos)).isConnected());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);

        if (!world.isRemote) {
            ((TileSlave) world.getTileEntity(pos)).onNeighborChanged(world);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileSlave machine = (TileSlave) world.getTileEntity(pos);

            if (machine.isConnected()) {
                machine.disconnect(world);
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        super.neighborChanged(state, world, pos, block);

        if (!world.isRemote) {
            ((TileSlave) world.getTileEntity(pos)).onNeighborChanged(world);
        }
    }
}
