package com.raoulvdberge.refinedstorage.block;

public class BlockImporter extends CableBlock {
    /* TODO
    public BlockImporter() {
        super(createBuilder("importer").tileEntity(TileImporter::new).create());
    }

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
                groups.add(ConstantsImporter.LINE_NORTH);
                break;
            case EAST:
                groups.add(ConstantsImporter.LINE_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsImporter.LINE_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsImporter.LINE_WEST);
                break;
            case UP:
                groups.add(ConstantsImporter.LINE_UP);
                break;
            case DOWN:
                groups.add(ConstantsImporter.LINE_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.IMPORTER, player, world, pos, side);
    }*/
}
