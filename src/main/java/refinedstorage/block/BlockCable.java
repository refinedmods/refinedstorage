package refinedstorage.block;

import com.google.common.collect.Lists;
import mcmultipart.block.BlockCoverable;
import mcmultipart.block.BlockMultipartContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileCable;
import refinedstorage.tile.TileMultipartNode;
import refinedstorage.tile.TileNode;

import java.util.List;

public class BlockCable extends BlockCoverable {
    private static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

    private static final AxisAlignedBB CABLE_AABB = new AxisAlignedBB(4 * (1F / 16F), 4 * (1F / 16F), 4 * (1F / 16F), 1 - 4 * (1F / 16F), 1 - 4 * (1F / 16F), 1 - 4 * (1F / 16F));

    private static final PropertyBool NORTH = PropertyBool.create("north");
    private static final PropertyBool EAST = PropertyBool.create("east");
    private static final PropertyBool SOUTH = PropertyBool.create("south");
    private static final PropertyBool WEST = PropertyBool.create("west");
    private static final PropertyBool UP = PropertyBool.create("up");
    private static final PropertyBool DOWN = PropertyBool.create("down");

    private String name;

    public BlockCable(String name) {
        super(Material.ROCK);

        this.name = name;

        setHardness(0.6F);
        setRegistryName(RefinedStorage.ID, name);
        setCreativeTab(RefinedStorage.INSTANCE.tab);
    }

    @Override
    public String getUnlocalizedName() {
        return "block." + RefinedStorage.ID + ":" + name;
    }

    public String getName() {
        return name;
    }

    public BlockCable() {
        this("cable");
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCable();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        builder.add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN)
            .add(BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER);

        if (getPlacementType() != null) {
            builder.add(DIRECTION);
        }

        return builder.build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos)
            .withProperty(NORTH, hasConnectionWith(world, pos, EnumFacing.NORTH))
            .withProperty(EAST, hasConnectionWith(world, pos, EnumFacing.EAST))
            .withProperty(SOUTH, hasConnectionWith(world, pos, EnumFacing.SOUTH))
            .withProperty(WEST, hasConnectionWith(world, pos, EnumFacing.WEST))
            .withProperty(UP, hasConnectionWith(world, pos, EnumFacing.UP))
            .withProperty(DOWN, hasConnectionWith(world, pos, EnumFacing.DOWN));

        if (getPlacementType() != null) {
            state = state.withProperty(DIRECTION, ((TileNode) world.getTileEntity(pos)).getDirection());
        }

        return state;
    }

    private boolean hasConnectionWith(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity facing = world.getTileEntity(pos.offset(direction));

        if (facing instanceof INetworkMaster || facing instanceof INetworkNode) {
            // Do not render a cable extension where our cable "head" is (e.g. importer, exporter, external storage heads).
            if (getPlacementType() != null) {
                return ((TileMultipartNode) world.getTileEntity(pos)).getFacingTile() != facing;
            }

            return !TileMultipartNode.hasBlockingMicroblock(world, pos, direction) && !TileMultipartNode.hasBlockingMicroblock(world, pos.offset(direction), direction.getOpposite());
        }

        return false;
    }

    /*@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CABLE_AABB;
    }*/

    private static AxisAlignedBB createaabb(int fromx, int fromy, int fromz, int tox, int toy, int toz) {
        return new AxisAlignedBB((float) fromx / 16F, (float) fromy / 16F, (float) fromz / 16F, (float) tox / 16F, (float) toy / 16F, (float) toz / 16F);
    }

    private static AxisAlignedBB core = createaabb(6, 6, 6, 10, 10, 10);
    private static AxisAlignedBB north = createaabb(6, 6, 0, 10, 10, 6);
    private static AxisAlignedBB east = createaabb(10, 6, 6, 16, 10, 10);
    private static AxisAlignedBB south = createaabb(6, 6, 10, 10, 10, 16);
    private static AxisAlignedBB west = createaabb(0, 6, 6, 6, 10, 10);
    private static AxisAlignedBB up = createaabb(6, 10, 6, 10, 16, 10);
    private static AxisAlignedBB down = createaabb(6, 0, 6, 10, 6, 10);

    private static List<AxisAlignedBB> getCollisionBoxList(IBlockState bstate) {
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();

        list.add(core);

        if (bstate.getValue(NORTH) == true) {
            list.add(north);
        }
        if (bstate.getValue(EAST) == true) {
            list.add(east);
        }
        if (bstate.getValue(SOUTH) == true) {
            list.add(south);
        }
        if (bstate.getValue(WEST) == true) {
            list.add(west);
        }
        if (bstate.getValue(UP) == true) {
            list.add(up);
        }
        if (bstate.getValue(DOWN) == true) {
            list.add(down);
        }

        return list;
    }


    @Override
    public void addCollisionBoxToListDefault(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(state, world, pos))) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    @Override
    public RayTraceResult collisionRayTraceDefault(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(state, world, pos))) {
            list.add(this.rayTrace(pos, start, end, axisalignedbb));
        }

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for (RayTraceResult raytraceresult : list) {
            if (raytraceresult != null) {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if (d0 > d1) {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    protected EnumPlacementType getPlacementType() {
        return null;
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity) {
        IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, entity);

        if (getPlacementType() != null) {
            return state.withProperty(DIRECTION, getPlacementType().getFrom(facing, entity));
        }

        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);

        if (getPlacementType() != null) {
            ((TileBase) world.getTileEntity(pos)).setDirection(state.getValue(DIRECTION));
        }

        attemptConnect(world, pos);
    }

    public void attemptConnect(World world, BlockPos pos) {
        if (!world.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile instanceof TileNode && ((TileNode) tile).isConnected()) {
                    NetworkUtils.rebuildGraph(((TileNode) tile).getNetwork());

                    break;
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        INetworkMaster network = null;

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileNode) {
                network = ((TileNode) tile).getNetwork();
            }

            if (tile instanceof TileBase && ((TileBase) tile).getDroppedItems() != null) {
                IItemHandler handler = ((TileBase) tile).getDroppedItems();

                for (int i = 0; i < handler.getSlots(); ++i) {
                    if (handler.getStackInSlot(i) != null) {
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                }
            }
        }

        super.breakBlock(world, pos, state);

        if (network != null) {
            NetworkUtils.rebuildGraph(network);
        }
    }

    @Override
    public boolean removedByPlayerDefault(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest ? true : super.removedByPlayerDefault(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlockDefault(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlockDefault(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!world.isRemote && getPlacementType() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            tile.setDirection(getPlacementType().getNext(tile.getDirection()));

            tile.updateBlock();

            return true;
        }

        return false;
    }
}
