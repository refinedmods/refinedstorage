package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.container.*;
import com.raoulvdberge.refinedstorage.gui.grid.GridDisplayDummy;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.externalstorage.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RSGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case RSGui.GRID:
                return new ContainerGrid((TileGrid) tile, new GridDisplayDummy(), player);
            case RSGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case RSGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case RSGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case RSGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case RSGui.SOLDERER:
                return new ContainerSolderer((TileSolderer) tile, player);
            case RSGui.DESTRUCTOR:
                return new ContainerDestructor((TileDestructor) tile, player);
            case RSGui.CONSTRUCTOR:
                return new ContainerConstructor((TileConstructor) tile, player);
            case RSGui.STORAGE:
                return new ContainerStorage((TileStorage) tile, player);
            case RSGui.EXTERNAL_STORAGE:
                return new ContainerExternalStorage((TileExternalStorage) tile, player);
            case RSGui.RELAY:
                return new ContainerRelay((TileRelay) tile, player);
            case RSGui.INTERFACE:
                return new ContainerInterface((TileInterface) tile, player);
            case RSGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor((TileCraftingMonitor) tile, player);
            case RSGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter((TileWirelessTransmitter) tile, player);
            case RSGui.CRAFTER:
                return new ContainerCrafter((TileCrafter) tile, player);
            case RSGui.PROCESSING_PATTERN_ENCODER:
                return new ContainerProcessingPatternEncoder((TileProcessingPatternEncoder) tile, player);
            case RSGui.NETWORK_TRANSMITTER:
                return new ContainerNetworkTransmitter((TileNetworkTransmitter) tile, player);
            case RSGui.FLUID_INTERFACE:
                return new ContainerFluidInterface((TileFluidInterface) tile, player);
            case RSGui.FLUID_STORAGE:
                return new ContainerFluidStorage((TileFluidStorage) tile, player);
            case RSGui.DISK_MANIPULATOR:
                return new ContainerDiskManipulator((TileDiskManipulator) tile, player);
            case RSGui.READER_WRITER:
                return new ContainerReaderWriter((IReaderWriter) tile, player);
            case RSGui.SECURITY_MANAGER:
                return new ContainerSecurityManager((TileSecurityManager) tile, player);
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RSGui.WIRELESS_GRID) {
            return getWirelessGridContainer(player, x, y);
        } else if (ID == RSGui.GRID_FILTER) {
            return getGridFilterContainer(player, x);
        } else if (ID == RSGui.WIRELESS_CRAFTING_MONITOR) {
            return getWirelessCraftingMonitorContainer(player, x, y);
        }

        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RSGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case RSGui.GRID:
                GuiGrid gui = new GuiGrid(null, (TileGrid) tile);
                gui.inventorySlots = new ContainerGrid((TileGrid) tile, gui, player);
                return gui;
            case RSGui.WIRELESS_GRID:
                return getWirelessGridGui(player, x, y);
            case RSGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), (IStorageGui) tile, "gui/disk_drive.png");
            case RSGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case RSGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case RSGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case RSGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile));
            case RSGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile));
            case RSGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile));
            case RSGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (TileStorage) tile);
            case RSGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), (TileExternalStorage) tile);
            case RSGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile));
            case RSGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile));
            case RSGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), (TileCraftingMonitor) tile);
            case RSGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile));
            case RSGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile));
            case RSGui.PROCESSING_PATTERN_ENCODER:
                return new GuiProcessingPatternEncoder((ContainerProcessingPatternEncoder) getContainer(ID, player, tile), (TileProcessingPatternEncoder) tile);
            case RSGui.GRID_FILTER:
                return new GuiGridFilter(getGridFilterContainer(player, x));
            case RSGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile);
            case RSGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile));
            case RSGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), (TileFluidStorage) tile);
            case RSGui.DISK_MANIPULATOR:
                return new GuiDiskManipulator((ContainerDiskManipulator) getContainer(ID, player, tile));
            case RSGui.WIRELESS_CRAFTING_MONITOR:
                return getWirelessCraftingMonitorGui(player, x, y);
            case RSGui.READER_WRITER:
                return new GuiReaderWriter((ContainerReaderWriter) getContainer(ID, player, tile), (IReaderWriter) tile);
            case RSGui.SECURITY_MANAGER:
                return new GuiSecurityManager((ContainerSecurityManager) getContainer(ID, player, tile), (TileSecurityManager) tile);
            default:
                return null;
        }
    }

    private WirelessGrid getWirelessGrid(EntityPlayer player, int hand, int controllerDimension) {
        return new WirelessGrid(controllerDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private GuiGrid getWirelessGridGui(EntityPlayer player, int hand, int controllerDimension) {
        WirelessGrid grid = getWirelessGrid(player, hand, controllerDimension);

        GuiGrid gui = new GuiGrid(null, grid);
        gui.inventorySlots = new ContainerGrid(grid, gui, player);
        return gui;
    }

    private ContainerGrid getWirelessGridContainer(EntityPlayer player, int hand, int controllerDimension) {
        return new ContainerGrid(getWirelessGrid(player, hand, controllerDimension), new GridDisplayDummy(), player);
    }

    private WirelessCraftingMonitor getWirelessCraftingMonitor(EntityPlayer player, int hand, int controllerDimension) {
        return new WirelessCraftingMonitor(controllerDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private GuiCraftingMonitor getWirelessCraftingMonitorGui(EntityPlayer player, int hand, int controllerDimension) {
        WirelessCraftingMonitor craftingMonitor = getWirelessCraftingMonitor(player, hand, controllerDimension);

        return new GuiCraftingMonitor(new ContainerCraftingMonitor(craftingMonitor, player), craftingMonitor);
    }

    private ContainerCraftingMonitor getWirelessCraftingMonitorContainer(EntityPlayer player, int hand, int controllerDimension) {
        return new ContainerCraftingMonitor(getWirelessCraftingMonitor(player, hand, controllerDimension), player);
    }

    private ContainerGridFilter getGridFilterContainer(EntityPlayer player, int hand) {
        return new ContainerGridFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }
}
