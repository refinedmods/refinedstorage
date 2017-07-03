package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public static final TileDataParameter<Integer> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.getNode().getSize();
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSize(value)) {
                tile.getNode().setSize(value);
                tile.getNode().markDirty();
            }
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
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

    public static final TileDataParameter<Boolean> PROCESSING_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return tile.getNode().isProcessingPattern();
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            tile.getNode().setProcessingPattern(value);
            tile.getNode().markDirty();

            tile.getNode().onPatternMatrixClear();

            tile.world.getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.openContainer instanceof ContainerGrid && ((ContainerGrid) player.openContainer).getTile() != null && ((ContainerGrid) player.openContainer).getTile().getPos().equals(tile.getPos()))
                .forEach(player -> {
                    ((ContainerGrid) player.openContainer).initSlots();
                    ((ContainerGrid) player.openContainer).sendAllSlots();
                });
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    });

    public static final TileDataParameter<Boolean> BLOCKING_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return tile.getNode().isBlockingPattern();
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            tile.getNode().setBlockingPattern(value);
            tile.getNode().markDirty();
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
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(OREDICT_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_PATTERN);
        dataManager.addWatchedParameter(BLOCKING_PATTERN);
    }

    @Override
    @Nonnull
    public NetworkNodeGrid createNode(World world, BlockPos pos) {
        return new NetworkNodeGrid(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeGrid.ID;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        return (getNode().getType() == GridType.PATTERN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (getNode().getType() == GridType.PATTERN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getPatterns());
        }

        return super.getCapability(capability, side);
    }
}
