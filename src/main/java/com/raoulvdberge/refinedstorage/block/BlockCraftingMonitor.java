package com.raoulvdberge.refinedstorage.block;

public class BlockCraftingMonitor extends NetworkNodeBlock {
    public BlockCraftingMonitor(Properties props) {
        super(props);
    }

    /*
        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerModels(IModelRegistration modelRegistration) {
            modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

            modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/crafting_monitor/cutouts/front_connected"));
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
            return openNetworkGui(RSGui.CRAFTING_MONITOR, player, world, pos, side, Permission.MODIFY, Permission.AUTOCRAFTING);
        }
    */
    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
