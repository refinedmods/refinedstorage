package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.tile.TileDestructor;

public class BlockDestructor extends BlockCable {
    public BlockDestructor() {
        super(createBuilder("destructor").tileEntity(TileDestructor::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCoverAndFullbright(modelRegistration, RS.ID + ":blocks/destructor/cutouts/connected");
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        return RSBlocks.CONSTRUCTOR.getCollisions(tile, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.DESTRUCTOR, player, world, pos, side);
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
