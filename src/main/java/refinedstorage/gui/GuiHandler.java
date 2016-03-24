package refinedstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import refinedstorage.RefinedStorageGui;
import refinedstorage.container.*;
import refinedstorage.storage.IStorageGui;
import refinedstorage.tile.*;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RefinedStorageGui.CONTROLLER:
                return new ContainerController(player);
            case RefinedStorageGui.GRID:
                return new ContainerGrid(player, (TileGrid) tile);
            case RefinedStorageGui.DRIVE:
                return new ContainerDrive(player, (TileDrive) tile);
            case RefinedStorageGui.IMPORTER:
                return new ContainerImporter(player, (TileImporter) tile);
            case RefinedStorageGui.EXPORTER:
                return new ContainerExporter(player, (TileExporter) tile);
            case RefinedStorageGui.DETECTOR:
                return new ContainerDetector(player, (TileDetector) tile);
            case RefinedStorageGui.SOLDERER:
                return new ContainerSolderer(player, (TileSolderer) tile);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter(player, (TileWirelessTransmitter) tile);
            case RefinedStorageGui.DESTRUCTOR:
                return new ContainerDestructor(player);
            case RefinedStorageGui.CONSTRUCTOR:
                return new ContainerConstructor(player, (TileConstructor) tile);
            case RefinedStorageGui.STORAGE:
                return new ContainerStorage(player, ((IStorageGui) tile).getInventory());
            case RefinedStorageGui.RELAY:
                return new ContainerRelay(player);
            case RefinedStorageGui.INTERFACE:
                return new ContainerInterface(player, (TileInterface) tile);
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
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
            case RefinedStorageGui.DRIVE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (IStorageGui) tile, "gui/drive.png");
            case RefinedStorageGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile), (TileImporter) tile);
            case RefinedStorageGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile), (TileExporter) tile);
            case RefinedStorageGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile), (TileDetector) tile);
            case RefinedStorageGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile), (TileSolderer) tile);
            case RefinedStorageGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile), (TileWirelessTransmitter) tile);
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
            default:
                return null;
        }
    }
}
