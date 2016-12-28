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

import javax.annotation.Nonnull;

public class TileGrid extends TileNode<NetworkNodeGrid> {
    public static final TileDataParameter<Integer> VIEW_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.getNode().getViewType();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidViewType(value)) {
                tile.getNode().setViewType(value);
                tile.getNode().markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.getNode().getSortingDirection();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingDirection(value)) {
                tile.getNode().setSortingDirection(value);
                tile.getNode().markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.getNode().getSortingType();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingType(value)) {
                tile.getNode().setSortingType(value);
                tile.getNode().markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.getNode().getSearchBoxMode();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSearchBoxMode(value)) {
                tile.getNode().setSearchBoxMode(value);
                tile.getNode().markDirty();
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
            return tile.getNode().getTabSelected();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            tile.getNode().setTabSelected(value == tile.getNode().getTabSelected() ? -1 : value);
            tile.getNode().markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).markForSorting();
        }
    });

    public static final TileDataParameter<Boolean> OREDICT_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return tile.getNode().isOredictPattern();
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            tile.getNode().setOredictPattern(value);
            tile.getNode().markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateOredictPattern(parameter.getValue());
        }
    });

    public TileGrid() {
        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(OREDICT_PATTERN);
    }

    @Override
    @Nonnull
    public NetworkNodeGrid createNode() {
        return new NetworkNodeGrid(this);
    }
}
