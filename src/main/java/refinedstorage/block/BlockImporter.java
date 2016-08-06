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
import refinedstorage.tile.TileImporter;

import java.util.ArrayList;
import java.util.List;

public class BlockImporter extends BlockCable {
    public static final AxisAlignedBB LINE_NORTH_1_AABB = createAABB(6, 6, 4, 10, 10, 6);
    public static final AxisAlignedBB LINE_NORTH_2_AABB = createAABB(5, 5, 2, 11, 11, 4);
    public static final AxisAlignedBB LINE_NORTH_3_AABB = createAABB(3, 3, 0, 13, 13, 2);
    public static final AxisAlignedBB LINE_EAST_1_AABB = createAABB(10, 6, 6, 12, 10, 10);
    public static final AxisAlignedBB LINE_EAST_2_AABB = createAABB(12, 5, 5, 14, 11, 11);
    public static final AxisAlignedBB LINE_EAST_3_AABB = createAABB(14, 3, 3, 16, 13, 13);
    public static final AxisAlignedBB LINE_SOUTH_1_AABB = createAABB(6, 6, 10, 10, 10, 12);
    public static final AxisAlignedBB LINE_SOUTH_2_AABB = createAABB(5, 5, 12, 11, 11, 14);
    public static final AxisAlignedBB LINE_SOUTH_3_AABB = createAABB(3, 3, 14, 13, 13, 16);
    public static final AxisAlignedBB LINE_WEST_1_AABB = createAABB(4, 6, 6, 6, 10, 10);
    public static final AxisAlignedBB LINE_WEST_2_AABB = createAABB(2, 5, 5, 4, 11, 11);
    public static final AxisAlignedBB LINE_WEST_3_AABB = createAABB(0, 3, 3, 2, 13, 13);
    public static final AxisAlignedBB LINE_UP_1_AABB = createAABB(6, 10, 6, 10, 12, 10);
    public static final AxisAlignedBB LINE_UP_2_AABB = createAABB(5, 12, 5, 11, 14, 11);
    public static final AxisAlignedBB LINE_UP_3_AABB = createAABB(3, 14, 3, 13, 16, 13);
    public static final AxisAlignedBB LINE_DOWN_1_AABB = createAABB(6, 4, 6, 10, 6, 10);
    public static final AxisAlignedBB LINE_DOWN_2_AABB = createAABB(5, 2, 5, 11, 4, 11);
    public static final AxisAlignedBB LINE_DOWN_3_AABB = createAABB(3, 0, 3, 13, 2, 13);

    public BlockImporter() {
        super("importer");
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        switch (state.getValue(DIRECTION)) {
            case NORTH:
                boxes.add(LINE_NORTH_1_AABB);
                boxes.add(LINE_NORTH_2_AABB);
                boxes.add(LINE_NORTH_3_AABB);
                break;
            case EAST:
                boxes.add(LINE_EAST_1_AABB);
                boxes.add(LINE_EAST_2_AABB);
                boxes.add(LINE_EAST_3_AABB);
                break;
            case SOUTH:
                boxes.add(LINE_SOUTH_1_AABB);
                boxes.add(LINE_SOUTH_2_AABB);
                boxes.add(LINE_SOUTH_3_AABB);
                break;
            case WEST:
                boxes.add(LINE_WEST_1_AABB);
                boxes.add(LINE_WEST_2_AABB);
                boxes.add(LINE_WEST_3_AABB);
                break;
            case UP:
                boxes.add(LINE_UP_1_AABB);
                boxes.add(LINE_UP_2_AABB);
                boxes.add(LINE_UP_3_AABB);
                break;
            case DOWN:
                boxes.add(LINE_DOWN_1_AABB);
                boxes.add(LINE_DOWN_2_AABB);
                boxes.add(LINE_DOWN_3_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileImporter();
    }

    @Override
    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.IMPORTER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.ANY;
    }
}
