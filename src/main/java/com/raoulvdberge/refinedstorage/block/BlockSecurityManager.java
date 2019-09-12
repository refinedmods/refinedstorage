package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;

public class BlockSecurityManager extends BlockNode {
    public BlockSecurityManager() {
        super(BlockInfoBuilder.forId("security_manager").tileEntity(TileSecurityManager::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/security_manager/cutouts/top_connected",
            RS.ID + ":blocks/security_manager/cutouts/front_connected",
            RS.ID + ":blocks/security_manager/cutouts/left_connected",
            RS.ID + ":blocks/security_manager/cutouts/back_connected",
            RS.ID + ":blocks/security_manager/cutouts/right_connected"
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
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.getGameProfile().getId().equals(((TileSecurityManager) world.getTileEntity(pos)).getNode().getOwner())) {
                player.openGui(RS.INSTANCE, RSGui.SECURITY_MANAGER, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                openNetworkGui(RSGui.SECURITY_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.SECURITY);
            }
        }

        return true;
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
