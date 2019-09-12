package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;

public class BlockCrafterManager extends BlockNode {
    public BlockCrafterManager() {
        super(BlockInfoBuilder.forId("crafter_manager").tileEntity(TileCrafterManager::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/crafter_manager/cutouts/front_connected"));
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
        if (!world.isRemote && openNetworkGui(RSGui.CRAFTER_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.AUTOCRAFTING)) {
            ((TileCrafterManager) world.getTileEntity(pos)).getNode().sendTo((ServerPlayerEntity) player);
        }

        return true;
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
