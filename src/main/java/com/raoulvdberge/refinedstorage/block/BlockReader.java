package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.tile.TileReader;

public class BlockReader extends BlockCable {
    public BlockReader() {
        super(createBuilder("reader").tileEntity(TileReader::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCoverAndFullbright(modelRegistration, RS.ID + ":blocks/reader/cutouts/connected");
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
            NetworkNodeReader reader = ((TileReader) world.getTileEntity(pos)).getNode();

            if (player.isSneaking()) {
                if (reader.getNetwork() != null) {
                    IReaderWriterChannel channel = reader.getNetwork().getReaderWriterManager().getChannel(reader.getChannel());

                    if (channel != null) {
                        channel.getHandlers().stream().map(h -> h.getStatusReader(reader, channel)).flatMap(List::stream).forEach(player::sendMessage);
                    }
                }
            } else {
                openNetworkGui(RSGui.READER_WRITER, player, world, pos, side);
            }
        }

        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof TileReader && side == ((TileReader) tile).getDirection().getOpposite();
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
