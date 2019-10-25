package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.enums.PortableGridType;
import com.raoulvdberge.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGridDiskState;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;

public class PortableGridBlock extends BaseBlock {
    public static final EnumProperty<PortableGridType> TYPE = EnumProperty.create("type", PortableGridType.class);
    public static final EnumProperty<PortableGridDiskState> DISK_STATE = EnumProperty.create("disk_state", PortableGridDiskState.class);
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private final PortableGridBlockItem.Type type;

    public PortableGridBlock(PortableGridBlockItem.Type type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.setRegistryName(RS.ID, (type == PortableGridBlockItem.Type.CREATIVE ? "creative_" : "") + "portable_grid");
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setStateMapper(this, new StateMap.Builder().ignore(TYPE).build());
        modelRegistration.setModelMeshDefinition(this, new ItemMeshDefinitionPortableGrid());

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/disks/leds"
        ));
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public Item createItem() {
        return new ItemBlockPortableGrid(this);
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        return Collections.singletonList(ConstantsPortableGrid.COLLISION);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            ((TilePortableGrid) world.getTileEntity(pos)).onPassItemContext(stack);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        drops.add(((TilePortableGrid) world.getTileEntity(pos)).getAsItem());
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
        TilePortableGrid portableGrid = (TilePortableGrid) world.getTileEntity(pos);

        return super.getActualState(state, world, pos)
            .withProperty(DISK_STATE, portableGrid.getDiskState())
            .withProperty(CONNECTED, portableGrid.isConnected());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .add(DISK_STATE)
            .add(CONNECTED)
            .build();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? PortableGridType.NORMAL : PortableGridType.CREATIVE);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(TYPE) == PortableGridType.NORMAL ? 0 : 1;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            API.instance().getGridManager().openGrid(TilePortableGrid.FACTORY_ID, (ServerPlayerEntity) player, pos);

            ((TilePortableGrid) world.getTileEntity(pos)).onOpened();
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }*/
}
