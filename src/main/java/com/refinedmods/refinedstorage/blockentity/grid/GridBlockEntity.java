package com.refinedmods.refinedstorage.blockentity.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GridBlockEntity extends NetworkNodeBlockEntity<GridNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Boolean, GridBlockEntity> EXACT_PATTERN = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_exact_mode"), EntityDataSerializers.BOOLEAN, true, t -> t.getNode().isExactPattern(), (t, v) -> {
        t.getNode().setExactPattern(v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.updateExactPattern(p)));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> PROCESSING_TYPE = IType.createParameter(new ResourceLocation(RS.ID, "grid_processing_type"), (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> VIEW_TYPE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_view_type"), EntityDataSerializers.INT, 0, t -> t.getNode().getViewType(), (t, v) -> {
        if (IGrid.isValidViewType(v)) {
            t.getNode().setViewType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final BlockEntitySynchronizationParameter<List<Set<ResourceLocation>>, GridBlockEntity> ALLOWED_ITEM_TAGS = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_allowed_item_tags"), RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedItemTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedItemTags(v));
    public static final BlockEntitySynchronizationParameter<List<Set<ResourceLocation>>, GridBlockEntity> ALLOWED_FLUID_TAGS = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_allowed_fluid_tags"), RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedFluidTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedFluidTags(v));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SORTING_DIRECTION = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_sorting_direction"), EntityDataSerializers.INT, 0, t -> t.getNode().getSortingDirection(), (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.getNode().setSortingDirection(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SORTING_TYPE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_sorting_type"), EntityDataSerializers.INT, 0, t -> t.getNode().getSortingType(), (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.getNode().setSortingType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> TAB_SELECTED = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_tab_selected"), EntityDataSerializers.INT, 0, t -> t.getNode().getTabSelected(), (t, v) -> {
        t.getNode().setTabSelected(v == t.getNode().getTabSelected() ? -1 : v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> TAB_PAGE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_tab_page"), EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0 && v <= t.getNode().getTotalTabPages()) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });
    public static final BlockEntitySynchronizationParameter<Boolean, GridBlockEntity> PROCESSING_PATTERN = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_processing_pattern"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isProcessingPattern(), (t, v) -> {
        t.getNode().setProcessingPattern(v);
        t.getNode().clearMatrix();
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SIZE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_size"), EntityDataSerializers.INT, 0, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.resize(grid.getMinecraft(), grid.width, grid.height)));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SEARCH_BOX_MODE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "grid_search_box_mode"), EntityDataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(VIEW_TYPE)
        .addWatchedParameter(SORTING_DIRECTION)
        .addWatchedParameter(SORTING_TYPE)
        .addWatchedParameter(SEARCH_BOX_MODE)
        .addWatchedParameter(SIZE)
        .addWatchedParameter(TAB_SELECTED)
        .addWatchedParameter(TAB_PAGE)
        .addWatchedParameter(EXACT_PATTERN)
        .addWatchedParameter(PROCESSING_PATTERN)
        .addWatchedParameter(PROCESSING_TYPE)
        .addParameter(ALLOWED_ITEM_TAGS)
        .addParameter(ALLOWED_FLUID_TAGS)
        .build();

    public static BlockEntityType<GridBlockEntity> getType(GridType type) {
        return switch (type) {
            case NORMAL -> RSBlockEntities.GRID.get();
            case CRAFTING -> RSBlockEntities.CRAFTING_GRID.get();
            case PATTERN -> RSBlockEntities.PATTERN_GRID.get();
            case FLUID -> RSBlockEntities.FLUID_GRID.get();
        };
    }

    public static void trySortGrid(boolean initial) {
        if (!initial) {
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    private final GridType type;

    public GridBlockEntity(GridType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state, SPEC, GridNetworkNode.class);
        this.type = type;
    }

    @Override
    @Nonnull
    public GridNetworkNode createNode(Level level, BlockPos pos) {
        return new GridNetworkNode(level, pos, type);
    }

    public IItemHandler getInventory() {
        if (type == GridType.PATTERN) {
            return getNode().getPatterns();
        }
        return null;
    }
}
