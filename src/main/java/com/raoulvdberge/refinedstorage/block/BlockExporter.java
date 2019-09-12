package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.tile.TileExporter;

public class BlockExporter extends BlockCable {
    public BlockExporter() {
        super(createBuilder("exporter").tileEntity(TileExporter::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCover(modelRegistration);
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsExporter.LINE_NORTH);
                break;
            case EAST:
                groups.add(ConstantsExporter.LINE_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsExporter.LINE_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsExporter.LINE_WEST);
                break;
            case UP:
                groups.add(ConstantsExporter.LINE_UP);
                break;
            case DOWN:
                groups.add(ConstantsExporter.LINE_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.EXPORTER, player, world, pos, side);
    }*/
}
