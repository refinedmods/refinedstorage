package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.tile.TileWriter;

public class BlockWriter extends BlockCable {
    public BlockWriter() {
        super(createBuilder("writer").tileEntity(TileWriter::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCoverAndFullbright(modelRegistration, RS.ID + ":blocks/writer/cutouts/connected");
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

        if (!world.isRemote) {
            NetworkNodeWriter writer = ((TileWriter) world.getTileEntity(pos)).getNode();

            if (player.isSneaking()) {
                if (writer.getNetwork() != null) {
                    IReaderWriterChannel channel = writer.getNetwork().getReaderWriterManager().getChannel(writer.getChannel());

                    if (channel != null) {
                        channel.getHandlers().stream().map(h -> h.getStatusWriter(writer, channel)).flatMap(List::stream).forEach(player::sendMessage);
                    }
                }
            } else {
                openNetworkGui(RSGui.READER_WRITER, player, world, pos, side);
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        IWriter writer = ((TileWriter) world.getTileEntity(pos)).getNode();

        return side == writer.getDirection().getOpposite() ? writer.getRedstoneStrength() : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        return getWeakPower(state, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof TileWriter && side == ((TileWriter) tile).getDirection().getOpposite();
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
