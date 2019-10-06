package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GridBlock extends NodeBlock {
    private final GridType type;

    public GridBlock(GridType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;

        this.setRegistryName(RS.ID, type == GridType.NORMAL ? "grid" : type.getName() + "_grid");
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GridTile(type);
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, GridType.CRAFTING.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=crafting"));
        modelRegistration.setModel(this, GridType.PATTERN.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=pattern"));
        modelRegistration.setModel(this, GridType.FLUID.getId(), new ModelResourceLocation(info.getId(), "connected=false,direction=north,type=fluid"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/grid/cutouts/front_connected",
            RS.ID + ":blocks/grid/cutouts/crafting_front_connected",
            RS.ID + ":blocks/grid/cutouts/pattern_front_connected",
            RS.ID + ":blocks/grid/cutouts/fluid_front_connected"
        ));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(player, world, pos, side, () -> API.instance().getGridManager().openGrid(NetworkNodeGrid.FACTORY_ID, (ServerPlayerEntity) player, pos));
    }*/
}
