package refinedstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import refinedstorage.RefinedStorageGui;
import refinedstorage.container.*;
import refinedstorage.gui.grid.GuiGrid;
import refinedstorage.tile.*;
import refinedstorage.tile.externalstorage.TileExternalStorage;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RefinedStorageGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case RefinedStorageGui.GRID:
                return new ContainerGrid((TileGrid) tile, player);
            case RefinedStorageGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case RefinedStorageGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case RefinedStorageGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case RefinedStorageGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case RefinedStorageGui.SOLDERER:
                return new ContainerSolderer((TileSolderer) tile, player);
            case RefinedStorageGui.DESTRUCTOR:
                return new ContainerDestructor((TileDestructor) tile, player);
            case RefinedStorageGui.CONSTRUCTOR:
                return new ContainerConstructor((TileConstructor) tile, player);
            case RefinedStorageGui.STORAGE:
                return new ContainerStorage((TileStorage) tile, player);
            case RefinedStorageGui.EXTERNAL_STORAGE:
                return new ContainerExternalStorage((TileExternalStorage) tile, player);
            case RefinedStorageGui.RELAY:
                return new ContainerRelay((TileRelay) tile, player);
            case RefinedStorageGui.INTERFACE:
                return new ContainerInterface((TileInterface) tile, player);
            case RefinedStorageGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor((TileCraftingMonitor) tile, player);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter((TileWirelessTransmitter) tile, player);
            case RefinedStorageGui.CRAFTER:
                return new ContainerCrafter((TileCrafter) tile, player);
            case RefinedStorageGui.PROCESSING_PATTERN_ENCODER:
                return new ContainerProcessingPatternEncoder((TileProcessingPatternEncoder) tile, player);
            case RefinedStorageGui.NETWORK_TRANSMITTER:
                return new ContainerNetworkTransmitter((TileNetworkTransmitter) tile, player);
            case RefinedStorageGui.FLUID_INTERFACE:
                return new ContainerFluidInterface((TileFluidInterface) tile, player);
            case RefinedStorageGui.FLUID_STORAGE:
                return new ContainerFluidStorage((TileFluidStorage) tile, player);
            case RefinedStorageGui.DISK_MANIPULATOR:
                return new ContainerDiskManipulator((TileDiskManipulator) tile, player);
            default:
                return null;
        }
    }

    private WirelessGrid getWirelessGrid(EntityPlayer player, int hand, int controllerDimension) {
        return new WirelessGrid(controllerDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private ContainerGrid getWirelessGridContainer(EntityPlayer player, int hand, int controllerDimension) {
        return new ContainerGrid(getWirelessGrid(player, hand, controllerDimension), player);
    }

    private ContainerGridFilter getGridFilterContainer(EntityPlayer player, int hand) {
        return new ContainerGridFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RefinedStorageGui.WIRELESS_GRID) {
            return getWirelessGridContainer(player, x, y);
        } else if (ID == RefinedStorageGui.GRID_FILTER) {
            return getGridFilterContainer(player, x);
        }

        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RefinedStorageGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case RefinedStorageGui.GRID:
                return new GuiGrid((ContainerGrid) getContainer(ID, player, tile), (TileGrid) tile);
            case RefinedStorageGui.WIRELESS_GRID:
                return getWirelessGridGui(player, x, y);
            case RefinedStorageGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), (IStorageGui) tile, "gui/disk_drive.png");
            case RefinedStorageGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case RefinedStorageGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case RefinedStorageGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case RefinedStorageGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile), (TileSolderer) tile);
            case RefinedStorageGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile));
            case RefinedStorageGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile));
            case RefinedStorageGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (TileStorage) tile);
            case RefinedStorageGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), (TileExternalStorage) tile);
            case RefinedStorageGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile));
            case RefinedStorageGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile));
            case RefinedStorageGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), (TileCraftingMonitor) tile);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile));
            case RefinedStorageGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile));
            case RefinedStorageGui.PROCESSING_PATTERN_ENCODER:
                return new GuiProcessingPatternEncoder((ContainerProcessingPatternEncoder) getContainer(ID, player, tile), (TileProcessingPatternEncoder) tile);
            case RefinedStorageGui.GRID_FILTER:
                return new GuiGridFilter(getGridFilterContainer(player, x));
            case RefinedStorageGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile);
            case RefinedStorageGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile));
            case RefinedStorageGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), (TileFluidStorage) tile);
            case RefinedStorageGui.DISK_MANIPULATOR:
                return new GuiDiskManipulator((ContainerDiskManipulator) getContainer(ID, player, tile));
            default:
                return null;
        }
    }

    private GuiGrid getWirelessGridGui(EntityPlayer player, int hand, int controllerDimension) {
        WirelessGrid grid = getWirelessGrid(player, hand, controllerDimension);

        return new GuiGrid(new ContainerGrid(grid, player), grid);
    }
}
