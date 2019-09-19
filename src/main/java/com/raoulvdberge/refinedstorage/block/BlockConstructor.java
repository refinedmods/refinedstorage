package com.raoulvdberge.refinedstorage.block;

public class BlockConstructor extends CableBlock {
    /* TODO
    public BlockConstructor() {
        super(createBuilder("constructor").tileEntity(TileConstructor::new).create());
    }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerModels(IModelRegistration modelRegistration) {
            modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

            registerCoverAndFullbright(modelRegistration, RS.ID + ":blocks/constructor/cutouts/connected");
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
                    groups.add(ConstantsConstructor.HEAD_NORTH);
                    break;
                case EAST:
                    groups.add(ConstantsCable.HOLDER_EAST);
                    groups.add(ConstantsConstructor.HEAD_EAST);
                    break;
                case SOUTH:
                    groups.add(ConstantsCable.HOLDER_SOUTH);
                    groups.add(ConstantsConstructor.HEAD_SOUTH);
                    break;
                case WEST:
                    groups.add(ConstantsCable.HOLDER_WEST);
                    groups.add(ConstantsConstructor.HEAD_WEST);
                    break;
                case UP:
                    groups.add(ConstantsCable.HOLDER_UP);
                    groups.add(ConstantsConstructor.HEAD_UP);
                    break;
                case DOWN:
                    groups.add(ConstantsCable.HOLDER_DOWN);
                    groups.add(ConstantsConstructor.HEAD_DOWN);
                    break;
            }

            return groups;
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
            if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
                return false;
            }

            return openNetworkGui(RSGui.CONSTRUCTOR, player, world, pos, side);
        }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
     */
}
