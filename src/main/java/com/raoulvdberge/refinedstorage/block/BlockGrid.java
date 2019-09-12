package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.state.EnumProperty;

public class BlockGrid extends BlockNode {
    public static final EnumProperty<GridType> TYPE = EnumProperty.create("type", GridType.class);

    public BlockGrid() {
        super(BlockInfoBuilder.forId("grid").tileEntity(TileGrid::new).create());
    }
/*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, GridType.NORMAL.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=normal"));
        modelRegistration.setModel(this, GridType.CRAFTING.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=crafting"));
        modelRegistration.setModel(this, GridType.PATTERN.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=pattern"));
        modelRegistration.setModel(this, GridType.FLUID.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=fluid"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/grid/cutouts/front_connected",
            RS.ID + ":blocks/grid/cutouts/crafting_front_connected",
            RS.ID + ":blocks/grid/cutouts/pattern_front_connected",
            RS.ID + ":blocks/grid/cutouts/fluid_front_connected"
        ));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 3; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .build();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? GridType.NORMAL : (meta == 1 ? GridType.CRAFTING : (meta == 2 ? GridType.PATTERN : GridType.FLUID)));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(TYPE) == GridType.NORMAL ? 0 : (state.getValue(TYPE) == GridType.CRAFTING ? 1 : (state.getValue(TYPE) == GridType.PATTERN ? 2 : 3));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(player, world, pos, side, () -> API.instance().getGridManager().openGrid(NetworkNodeGrid.FACTORY_ID, (ServerPlayerEntity) player, pos));
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
