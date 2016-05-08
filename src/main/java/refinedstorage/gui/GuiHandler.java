package refinedstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.*;
import refinedstorage.storage.IStorageGui;
import refinedstorage.tile.*;
import refinedstorage.tile.autocrafting.TileCrafter;
import refinedstorage.tile.autocrafting.TileCraftingMonitor;
import refinedstorage.tile.autocrafting.TileProcessingPatternEncoder;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RefinedStorageGui.CONTROLLER:
                return new ContainerController(player);
            case RefinedStorageGui.GRID:
                return new ContainerGrid(player, (TileGrid) tile);
            case RefinedStorageGui.DISK_DRIVE:
                return new ContainerDiskDrive(player, (TileDiskDrive) tile);
            case RefinedStorageGui.IMPORTER:
                return new ContainerImporter(player, (TileImporter) tile);
            case RefinedStorageGui.EXPORTER:
                return new ContainerExporter(player, (TileExporter) tile);
            case RefinedStorageGui.DETECTOR:
                return new ContainerDetector(player, (TileDetector) tile);
            case RefinedStorageGui.SOLDERER:
                return new ContainerSolderer(player, (TileSolderer) tile);
            case RefinedStorageGui.DESTRUCTOR:
                return new ContainerDestructor(player, (TileDestructor) tile);
            case RefinedStorageGui.CONSTRUCTOR:
                return new ContainerConstructor(player, (TileConstructor) tile);
            case RefinedStorageGui.STORAGE:
                return new ContainerStorage(player, ((IStorageGui) tile).getInventory());
            case RefinedStorageGui.RELAY:
                return new ContainerRelay(player);
            case RefinedStorageGui.INTERFACE:
                return new ContainerInterface(player, (TileInterface) tile);
            case RefinedStorageGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor(player);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter(player, (TileWirelessTransmitter) tile);
            case RefinedStorageGui.CRAFTER:
                return new ContainerCrafter(player, (TileCrafter) tile);
            case RefinedStorageGui.PROCESSING_PATTERN_ENCODER:
                return new ContainerProcessingPatternEncoder(player, (TileProcessingPatternEncoder) tile);
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RefinedStorageGui.WIRELESS_GRID) {
            return getWirelessGridContainer(player, x);
        }

        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    private WirelessGrid getWirelessGrid(EntityPlayer player, int hand) {
        return new WirelessGrid(player.getHeldItem(RefinedStorageUtils.getHandById(hand)), RefinedStorageUtils.getHandById(hand));
    }

    private ContainerGrid getWirelessGridContainer(EntityPlayer player, int hand) {
        WirelessGrid wirelessGrid = getWirelessGrid(player, hand);

        return new ContainerGrid(player, wirelessGrid);
    }

    private GuiGrid getWirelessGridGui(EntityPlayer player, int hand) {
        WirelessGrid wirelessGrid = getWirelessGrid(player, hand);

        return new GuiGrid(new ContainerGrid(player, wirelessGrid), wirelessGrid);
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
                return getWirelessGridGui(player, x);
            case RefinedStorageGui.DISK_DRIVE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (IStorageGui) tile, "gui/disk_drive.png");
            case RefinedStorageGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile), (TileImporter) tile);
            case RefinedStorageGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile), (TileExporter) tile);
            case RefinedStorageGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile), (TileDetector) tile);
            case RefinedStorageGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile), (TileSolderer) tile);
            case RefinedStorageGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile), (TileDestructor) tile);
            case RefinedStorageGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile), (TileConstructor) tile);
            case RefinedStorageGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (IStorageGui) tile);
            case RefinedStorageGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile), (TileRelay) tile);
            case RefinedStorageGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile), (TileInterface) tile);
            case RefinedStorageGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), (TileCraftingMonitor) tile);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile), (TileWirelessTransmitter) tile);
            case RefinedStorageGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile), (TileCrafter) tile);
            case RefinedStorageGui.PROCESSING_PATTERN_ENCODER:
                return new GuiProcessingPatternEncoder((ContainerProcessingPatternEncoder) getContainer(ID, player, tile), (TileProcessingPatternEncoder) tile);
            default:
                return null;
        }
    }
}
