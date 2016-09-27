package refinedstorage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileDestructor;

import java.util.List;

// @TODO: Destructor connected / disconnected
public class BlockDestructor extends BlockCable {
    public BlockDestructor(String name) {
        super(name);
    }

    public BlockDestructor() {
        this("destructor");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDestructor();
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        return RefinedStorageBlocks.CONSTRUCTOR.getNonUnionizedCollisionBoxes(state);
    }

    @Override
    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.DESTRUCTOR, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.ANY;
    }
}
