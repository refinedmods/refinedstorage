package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.tile.TileCable;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.CollisionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class BlockCable extends BlockNode {
    /* TODO
    public static final PropertyObject<Cover> COVER_NORTH = new PropertyObject<>("cover_north", Cover.class);
    public static final PropertyObject<Cover> COVER_EAST = new PropertyObject<>("cover_east", Cover.class);
    public static final PropertyObject<Cover> COVER_SOUTH = new PropertyObject<>("cover_south", Cover.class);
    public static final PropertyObject<Cover> COVER_WEST = new PropertyObject<>("cover_west", Cover.class);
    public static final PropertyObject<Cover> COVER_UP = new PropertyObject<>("cover_up", Cover.class);
    public static final PropertyObject<Cover> COVER_DOWN = new PropertyObject<>("cover_down", Cover.class);*/

    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");

    public BlockCable(IBlockInfo info) {
        super(info);
    }

    public BlockCable() {
        super(createBuilder("cable").tileEntity(TileCable::new).create());
    }

    static BlockInfoBuilder createBuilder(String id) {
        return BlockInfoBuilder.forId(id).material(Material.GLASS).soundType(SoundType.GLASS).hardness(0.35F);
    }

    @OnlyIn(Dist.CLIENT)
    void registerCover(IModelRegistration modelRegistration) {
        // TODO modelRegistration.addBakedModelOverride(info.getId(), BakedModelCableCover::new);
    }

    @OnlyIn(Dist.CLIENT)
    void registerCoverAndFullbright(IModelRegistration modelRegistration, ResourceLocation... textures) {
        // TODO modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelCableCover(new BakedModelFullbright(base, textures)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCover(modelRegistration);
    }

    @Override
    public boolean hasConnectedState() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    /* TODO
    @Override
    @SuppressWarnings("deprecation")
    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        state = super.getActualState(state, world, pos)
            .withProperty(NORTH, hasConnectionWith(world, pos, this, tile, Direction.NORTH))
            .withProperty(EAST, hasConnectionWith(world, pos, this, tile, Direction.EAST))
            .withProperty(SOUTH, hasConnectionWith(world, pos, this, tile, Direction.SOUTH))
            .withProperty(WEST, hasConnectionWith(world, pos, this, tile, Direction.WEST))
            .withProperty(UP, hasConnectionWith(world, pos, this, tile, Direction.UP))
            .withProperty(DOWN, hasConnectionWith(world, pos, this, tile, Direction.DOWN));

        return state;
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
        BlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            s = ((IExtendedBlockState) s).withProperty(COVER_NORTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.NORTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_EAST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.EAST));
            s = ((IExtendedBlockState) s).withProperty(COVER_SOUTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.SOUTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_WEST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.WEST));
            s = ((IExtendedBlockState) s).withProperty(COVER_UP, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.UP));
            s = ((IExtendedBlockState) s).withProperty(COVER_DOWN, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.DOWN));
        }

        return s;
    }*/

    private static boolean hasConnectionWith(World world, BlockPos pos, BlockBase block, TileEntity tile, Direction direction) {
        if (!(tile instanceof TileNode)) {
            return false;
        }

        INetworkNode node = ((TileNode) tile).getNode();

        if (node instanceof ICoverable) {
            Cover cover = ((ICoverable) node).getCoverManager().getCover(direction);

            if (cover != null && cover.getType() != CoverType.HOLLOW) {
                return false;
            }
        }

        TileEntity otherTile = world.getTileEntity(pos.offset(direction));

        if (otherTile instanceof TileNode && ((TileNode) otherTile).getNode() instanceof ICoverable) {
            Cover cover = ((ICoverable) ((TileNode) otherTile).getNode()).getCoverManager().getCover(direction.getOpposite());

            if (cover != null && cover.getType() != CoverType.HOLLOW) {
                return false;
            }
        }

        /* TODO
        if (otherTile != null && otherTile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite())) {
            // Prevent the block adding connections in itself
            // For example: importer cable connection on the importer face
            if (block.getDirection() != null && ((TileBase) tile).getDirection() == direction) {
                return false;
            }

            return true;
        } */

        return false;
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        List<CollisionGroup> groups = getCoverCollisions(tile);

        groups.add(ConstantsCable.CORE);

        /* TODO if (state.getValue(NORTH)) {
            groups.add(ConstantsCable.NORTH);
        }

        if (state.getValue(EAST)) {
            groups.add(ConstantsCable.EAST);
        }

        if (state.getValue(SOUTH)) {
            groups.add(ConstantsCable.SOUTH);
        }

        if (state.getValue(WEST)) {
            groups.add(ConstantsCable.WEST);
        }

        if (state.getValue(UP)) {
            groups.add(ConstantsCable.UP);
        }

        if (state.getValue(DOWN)) {
            groups.add(ConstantsCable.DOWN);
        } */

        return groups;
    }

    private List<CollisionGroup> getCoverCollisions(TileEntity tile) {
        List<CollisionGroup> groups = new ArrayList<>();

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager coverManager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            Cover coverNorth = coverManager.getCover(Direction.NORTH);
            Cover coverEast = coverManager.getCover(Direction.EAST);
            Cover coverSouth = coverManager.getCover(Direction.SOUTH);
            Cover coverWest = coverManager.getCover(Direction.WEST);
            Cover coverUp = coverManager.getCover(Direction.UP);
            Cover coverDown = coverManager.getCover(Direction.DOWN);

            if (coverNorth != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    coverWest != null ? 2 : 0, coverDown != null ? 2 : 0, 0,
                    coverEast != null ? 14 : 16, coverUp != null ? 14 : 16, 2
                )).setDirection(Direction.NORTH));

                if (coverNorth.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_NORTH);
                }
            }

            if (coverEast != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    14, coverDown != null ? 2 : 0, 0,
                    16, coverUp != null ? 14 : 16, 16
                )).setDirection(Direction.EAST));

                if (coverEast.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_EAST);
                }
            }

            if (coverSouth != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    coverEast != null ? 14 : 16, coverDown != null ? 2 : 0, 16,
                    coverWest != null ? 2 : 0, coverUp != null ? 14 : 16, 14
                )).setDirection(Direction.SOUTH));

                if (coverSouth.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_SOUTH);
                }
            }

            if (coverWest != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, coverDown != null ? 2 : 0, 0,
                    2, coverUp != null ? 14 : 16, 16
                )).setDirection(Direction.WEST));

                if (coverWest.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_WEST);
                }
            }

            if (coverUp != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, 14, 0,
                    16, 16, 16
                )).setDirection(Direction.UP));

                if (coverUp.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_UP);
                }
            }

            if (coverDown != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, 0, 0,
                    16, 2, 16
                )).setDirection(Direction.DOWN));

                if (coverDown.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_DOWN);
                }
            }
        }

        return groups;
    }

    /* TODO
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
    @SuppressWarnings("deprecation")
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, entity);

        if (getDirection() != null) {
            return state.withProperty(getDirection().getProperty(), getDirection().getFrom(facing, pos, entity));
        }

        return state;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }*/
}
