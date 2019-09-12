package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileWirelessTransmitter;

public class BlockWirelessTransmitter extends BlockNode {
    public BlockWirelessTransmitter() {
        super(BlockInfoBuilder.forId("wireless_transmitter").tileEntity(TileWirelessTransmitter::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/wireless_transmitter/cutouts/connected"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.WIRELESS_TRANSMITTER, player, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == this) {
            dropBlockAsItem(world, pos, state, 0);

            world.setBlockToAir(pos);
        }
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        return Collections.singletonList(ConstantsWirelessTransmitter.COLLISION);
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
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos.offset(Direction.DOWN));

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, Direction.UP)) {
            INetworkNodeProxy proxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, Direction.UP);

            if (proxy != null && proxy.getNode() instanceof INetworkNodeCable) {
                return true;
            }
        }

        return world.getBlockState(pos.offset(Direction.DOWN)).getBlock() instanceof BlockCable; // Make sure we still detect stuff like importers/exporters/etc.
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(I18n.format("block.refinedstorage:wireless_transmitter.tooltip", TextFormatting.WHITE + I18n.format("block.refinedstorage:cable.name") + TextFormatting.GRAY));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    } */
}
