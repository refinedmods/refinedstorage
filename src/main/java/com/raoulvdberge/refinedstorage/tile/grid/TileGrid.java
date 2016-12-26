package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataSerializers;

public class TileGrid extends TileNode {
    public static final TileDataParameter<Integer> VIEW_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).getViewType();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidViewType(value)) {
                NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

                grid.setViewType(value);
                grid.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).getSortingDirection();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingDirection(value)) {
                NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

                grid.setSortingDirection(value);
                grid.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).getSortingType();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingType(value)) {
                NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

                grid.setSortingType(value);
                grid.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).getSearchBoxMode();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSearchBoxMode(value)) {
                NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

                grid.setSearchBoxMode(value);
                grid.markDirty();
            }
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateSearchFieldFocus(parameter.getValue());
        }
    });

    public static final TileDataParameter<Integer> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).getTabSelected();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

            grid.setTabSelected(value == grid.getTabSelected() ? -1 : value);
            grid.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).markForSorting();
        }
    });

    public static final TileDataParameter<Boolean> OREDICT_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).isOredictPattern();
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

            grid.setOredictPattern(value);
            grid.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateOredictPattern(parameter.getValue());
        }
    });

    public static final TileDataParameter<Boolean> BLOCKING_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return ((NetworkNodeGrid) tile.getNode()).isBlockingPattern();
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            NetworkNodeGrid grid = (NetworkNodeGrid) tile.getNode();

            grid.setBlockingPattern(value);
            grid.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateBlockingPattern(parameter.getValue());
        }
    });

    public TileGrid() {
        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(OREDICT_PATTERN);
        dataManager.addWatchedParameter(BLOCKING_PATTERN);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeGrid(this);
    }
}
