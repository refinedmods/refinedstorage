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
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileConstructor;

import java.util.ArrayList;
import java.util.List;

public class BlockConstructor extends BlockCable {
    public static final AxisAlignedBB HOLDER_NORTH_AABB = createAABB(7, 7, 2, 9, 9, 6);
    public static final AxisAlignedBB HOLDER_EAST_AABB = createAABB(10, 7, 7, 14, 9, 9);
    public static final AxisAlignedBB HOLDER_SOUTH_AABB = createAABB(7, 7, 10, 9, 9, 14);
    public static final AxisAlignedBB HOLDER_WEST_AABB = createAABB(2, 7, 7, 6, 9, 9);
    public static final AxisAlignedBB HOLDER_UP_AABB = createAABB(7, 10, 7, 9, 14, 9);
    public static final AxisAlignedBB HOLDER_DOWN_AABB = createAABB(7, 2, 7, 9, 6, 9);

    public static final AxisAlignedBB HEAD_NORTH_AABB = createAABB(0, 0, 0, 16, 16, 2);
    public static final AxisAlignedBB HEAD_EAST_AABB = createAABB(14, 0, 0, 16, 16, 16);
    public static final AxisAlignedBB HEAD_SOUTH_AABB = createAABB(0, 0, 14, 16, 16, 16);
    public static final AxisAlignedBB HEAD_WEST_AABB = createAABB(0, 0, 0, 2, 16, 16);
    public static final AxisAlignedBB HEAD_DOWN_AABB = createAABB(0, 0, 0, 16, 2, 16);
    public static final AxisAlignedBB HEAD_UP_AABB = createAABB(0, 14, 0, 16, 16, 16);

    public BlockConstructor(String name) {
        super(name);
    }

    public BlockConstructor() {
        this("constructor");
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        switch (state.getValue(DIRECTION)) {
            case NORTH:
                boxes.add(HOLDER_NORTH_AABB);
                boxes.add(HEAD_NORTH_AABB);
                break;
            case EAST:
                boxes.add(HOLDER_EAST_AABB);
                boxes.add(HEAD_EAST_AABB);
                break;
            case SOUTH:
                boxes.add(HOLDER_SOUTH_AABB);
                boxes.add(HEAD_SOUTH_AABB);
                break;
            case WEST:
                boxes.add(HOLDER_WEST_AABB);
                boxes.add(HEAD_WEST_AABB);
                break;
            case UP:
                boxes.add(HOLDER_UP_AABB);
                boxes.add(HEAD_UP_AABB);
                break;
            case DOWN:
                boxes.add(HOLDER_DOWN_AABB);
                boxes.add(HEAD_DOWN_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileConstructor();
    }

    @Override
    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.CONSTRUCTOR, world, pos.getX(), pos.getY(), pos.getZ());
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
