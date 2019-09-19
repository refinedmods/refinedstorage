package com.raoulvdberge.refinedstorage.block;

public class BlockExternalStorage extends CableBlock {
    /* TODO
    public BlockExternalStorage() {
        super(createBuilder("external_storage").tileEntity(TileExternalStorage::new).create());
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
                groups.add(ConstantsCable.HOLDER_NORTH);
                groups.add(ConstantsExternalStorage.HEAD_NORTH);
                break;
            case EAST:
                groups.add(ConstantsCable.HOLDER_EAST);
                groups.add(ConstantsExternalStorage.HEAD_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsCable.HOLDER_SOUTH);
                groups.add(ConstantsExternalStorage.HEAD_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsCable.HOLDER_WEST);
                groups.add(ConstantsExternalStorage.HEAD_WEST);
                break;
            case UP:
                groups.add(ConstantsCable.HOLDER_UP);
                groups.add(ConstantsExternalStorage.HEAD_UP);
                break;
            case DOWN:
                groups.add(ConstantsCable.HOLDER_DOWN);
                groups.add(ConstantsExternalStorage.HEAD_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.EXTERNAL_STORAGE, player, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileExternalStorage) {
                NetworkNodeExternalStorage externalStorage = ((TileExternalStorage) tile).getNode();

                if (externalStorage.getNetwork() != null) {
                    externalStorage.updateStorage(externalStorage.getNetwork());
                }
            }
        }
    }*/
}
