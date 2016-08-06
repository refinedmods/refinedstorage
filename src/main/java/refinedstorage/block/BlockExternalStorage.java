package refinedstorage.block;

import net.minecraft.block.Block;
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
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.externalstorage.TileExternalStorage;

import java.util.ArrayList;
import java.util.List;

public class BlockExternalStorage extends BlockCable {
    public static final AxisAlignedBB HEAD_NORTH_AABB = createAABB(3, 3, 0, 13, 13, 2);
    public static final AxisAlignedBB HEAD_EAST_AABB = createAABB(14, 3, 3, 16, 13, 13);
    public static final AxisAlignedBB HEAD_SOUTH_AABB = createAABB(3, 3, 14, 13, 13, 16);
    public static final AxisAlignedBB HEAD_WEST_AABB = createAABB(0, 3, 3, 2, 13, 13);
    public static final AxisAlignedBB HEAD_UP_AABB = createAABB(3, 14, 3, 13, 16, 13);
    public static final AxisAlignedBB HEAD_DOWN_AABB = createAABB(3, 0, 3, 13, 2, 13);

    public BlockExternalStorage() {
        super("external_storage");
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        switch (state.getValue(DIRECTION)) {
            case NORTH:
                boxes.add(BlockConstructor.HOLDER_NORTH_AABB);
                boxes.add(HEAD_NORTH_AABB);
                break;
            case EAST:
                boxes.add(BlockConstructor.HOLDER_EAST_AABB);
                boxes.add(HEAD_EAST_AABB);
                break;
            case SOUTH:
                boxes.add(BlockConstructor.HOLDER_SOUTH_AABB);
                boxes.add(HEAD_SOUTH_AABB);
                break;
            case WEST:
                boxes.add(BlockConstructor.HOLDER_WEST_AABB);
                boxes.add(HEAD_WEST_AABB);
                break;
            case UP:
                boxes.add(BlockConstructor.HOLDER_UP_AABB);
                boxes.add(HEAD_UP_AABB);
                break;
            case DOWN:
                boxes.add(BlockConstructor.HOLDER_DOWN_AABB);
                boxes.add(HEAD_DOWN_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExternalStorage();
    }

    @Override
    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onNeighborBlockChangeDefault(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChangeDefault(world, pos, state, neighborBlock);

        if (!world.isRemote) {
            TileExternalStorage externalStorage = (TileExternalStorage) world.getTileEntity(pos);

            if (externalStorage.getNetwork() != null) {
                externalStorage.updateStorage(externalStorage.getNetwork());
            }
        }
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.ANY;
    }
}
