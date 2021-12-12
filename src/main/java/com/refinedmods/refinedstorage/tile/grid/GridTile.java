package com.refinedmods.refinedstorage.tile.grid;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GridTile extends NetworkNodeTile<GridNetworkNode> {
    public static final TileDataParameter<Boolean, GridTile> EXACT_PATTERN = new TileDataParameter<>(EntityDataSerializers.BOOLEAN, true, t -> t.getNode().isExactPattern(), (t, v) -> {
        t.getNode().setExactPattern(v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.updateExactPattern(p)));
    public static final TileDataParameter<Integer, GridTile> VIEW_TYPE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getViewType(), (t, v) -> {
        if (IGrid.isValidViewType(v)) {
            t.getNode().setViewType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, GridTile> PROCESSING_TYPE = IType.createParameter((initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final TileDataParameter<Integer, GridTile> SORTING_DIRECTION = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSortingDirection(), (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.getNode().setSortingDirection(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<List<Set<ResourceLocation>>, GridTile> ALLOWED_ITEM_TAGS = new TileDataParameter<>(RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedItemTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedItemTags(v));
    public static final TileDataParameter<Integer, GridTile> SORTING_TYPE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSortingType(), (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.getNode().setSortingType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<List<Set<ResourceLocation>>, GridTile> ALLOWED_FLUID_TAGS = new TileDataParameter<>(RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedFluidTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedFluidTags(v));
    public static final TileDataParameter<Integer, GridTile> SEARCH_BOX_MODE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));
    private final GridType type;
    public static final TileDataParameter<Integer, GridTile> SIZE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.resize(grid.getMinecraft(), grid.width, grid.height)));
    private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getPatterns());
    public static final TileDataParameter<Integer, GridTile> TAB_SELECTED = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabSelected(), (t, v) -> {
        t.getNode().setTabSelected(v == t.getNode().getTabSelected() ? -1 : v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));
    public static final TileDataParameter<Integer, GridTile> TAB_PAGE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0 && v <= t.getNode().getTotalTabPages()) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Boolean, GridTile> PROCESSING_PATTERN = new TileDataParameter<>(EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isProcessingPattern(), (t, v) -> {
        t.getNode().setProcessingPattern(v);
        t.getNode().clearMatrix();
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));

    public GridTile(GridType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state);

        this.type = type;

        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
        dataManager.addWatchedParameter(EXACT_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_TYPE);
        dataManager.addParameter(ALLOWED_ITEM_TAGS);
        dataManager.addParameter(ALLOWED_FLUID_TAGS);
    }


    public static void trySortGrid(boolean initial) {
        if (!initial) {
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    public static BlockEntityType<GridTile> getType(GridType type) {
        switch (type) {
            case NORMAL:
                return RSTiles.GRID;
            case CRAFTING:
                return RSTiles.CRAFTING_GRID;
            case PATTERN:
                return RSTiles.PATTERN_GRID;
            case FLUID:
                return RSTiles.FLUID_GRID;
            default:
                throw new IllegalArgumentException("Unknown grid type " + type);
        }
    }

    @Override
    @Nonnull
    public GridNetworkNode createNode(Level world, BlockPos pos) {
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
