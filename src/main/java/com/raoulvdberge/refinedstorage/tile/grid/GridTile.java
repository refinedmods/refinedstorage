package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.tile.NetworkNodeTile;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.GridUtils;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GridTile extends NetworkNodeTile<GridNetworkNode> {
    public static final TileDataParameter<Integer, GridTile> VIEW_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getViewType(), (t, v) -> {
        if (IGrid.isValidViewType(v)) {
            t.getNode().setViewType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, GridTile> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getSortingDirection(), (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.getNode().setSortingDirection(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, GridTile> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getSortingType(), (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.getNode().setSortingType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, GridTile> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));
    public static final TileDataParameter<Integer, GridTile> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final TileDataParameter<Integer, GridTile> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getTabSelected(), (t, v) -> {
        t.getNode().setTabSelected(v == t.getNode().getTabSelected() ? -1 : v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));
    public static final TileDataParameter<Integer, GridTile> TAB_PAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0 && v <= t.getNode().getTotalTabPages()) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Boolean, GridTile> OREDICT_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isOredictPattern(), (t, v) -> {
        t.getNode().setOredictPattern(v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.updateOredictPattern(p)));
    public static final TileDataParameter<Boolean, GridTile> PROCESSING_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isProcessingPattern(), (t, v) -> {
        t.getNode().setProcessingPattern(v);
        t.getNode().clearMatrix();
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final TileDataParameter<Integer, GridTile> PROCESSING_TYPE = IType.createParameter((initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));

    public static void trySortGrid(boolean initial) {
        if (!initial) {
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    private final GridType type;

    private LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getPatterns());

    public GridTile(GridType type) {
        super(GridUtils.getTileEntityType(type));

        this.type = type;

        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
        dataManager.addWatchedParameter(OREDICT_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_TYPE);
    }

    @Override
    @Nonnull
    public GridNetworkNode createNode(World world, BlockPos pos) {
        return new GridNetworkNode(world, pos, type);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && type == GridType.PATTERN) {
            return diskCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
