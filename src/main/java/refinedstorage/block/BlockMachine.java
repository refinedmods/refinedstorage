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
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.controller.ControllerSearcher;
import refinedstorage.tile.controller.TileController;

import java.util.HashSet;

public abstract class BlockMachine extends BlockBase {
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public BlockMachine(String name) {
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
            .withProperty(CONNECTED, ((TileMachine) world.getTileEntity(pos)).isConnected());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);

        if (!world.isRemote) {
            TileController controller = ControllerSearcher.search(world, pos, new HashSet<String>());

            if (controller != null) {
                ((TileMachine) world.getTileEntity(pos)).onConnected(world, controller);
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileMachine machine = (TileMachine) world.getTileEntity(pos);

            if (machine.isConnected()) {
                machine.onDisconnected(world);
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        super.neighborChanged(state, world, pos, block);

        if (!world.isRemote) {
            ((TileMachine) world.getTileEntity(pos)).searchController(world);
        }
    }
}
