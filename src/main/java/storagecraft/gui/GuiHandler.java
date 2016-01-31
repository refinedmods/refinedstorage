package storagecraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import storagecraft.StorageCraftGUI;
import storagecraft.container.*;
import storagecraft.storage.IStorageGui;
import storagecraft.tile.*;

public class GuiHandler implements IGuiHandler
{
	private Container getContainer(int ID, EntityPlayer player, TileEntity tile)
	{
		switch (ID)
		{
			case StorageCraftGUI.CONTROLLER:
				return new ContainerController(player);
			case StorageCraftGUI.GRID:
				return new ContainerGrid(player, (TileGrid) tile);
			case StorageCraftGUI.DRIVE:
				return new ContainerDrive(player, (TileDrive) tile);
			case StorageCraftGUI.IMPORTER:
				return new ContainerImporter(player, (TileImporter) tile);
			case StorageCraftGUI.EXPORTER:
				return new ContainerExporter(player, (TileExporter) tile);
			case StorageCraftGUI.DETECTOR:
				return new ContainerDetector(player, (TileDetector) tile);
			case StorageCraftGUI.SOLDERER:
				return new ContainerSolderer(player, (TileSolderer) tile);
			case StorageCraftGUI.WIRELESS_TRANSMITTER:
				return new ContainerWirelessTransmitter(player, (TileWirelessTransmitter) tile);
			case StorageCraftGUI.DESTRUCTOR:
				return new ContainerDestructor(player);
			case StorageCraftGUI.CONSTRUCTOR:
				return new ContainerConstructor(player, (TileConstructor) tile);
			case StorageCraftGUI.STORAGE:
				return new ContainerStorage(player, ((IStorageGui) tile).getInventory());
			default:
				return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID)
		{
			case StorageCraftGUI.CONTROLLER:
				return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
			case StorageCraftGUI.GRID:
				return new GuiGrid((ContainerGrid) getContainer(ID, player, tile), (TileGrid) tile);
			case StorageCraftGUI.DRIVE:
				return new GuiDrive((ContainerDrive) getContainer(ID, player, tile), (TileDrive) tile);
			case StorageCraftGUI.IMPORTER:
				return new GuiImporter((ContainerImporter) getContainer(ID, player, tile), (TileImporter) tile);
			case StorageCraftGUI.EXPORTER:
				return new GuiExporter((ContainerExporter) getContainer(ID, player, tile), (TileExporter) tile);
			case StorageCraftGUI.DETECTOR:
				return new GuiDetector((ContainerDetector) getContainer(ID, player, tile), (TileDetector) tile);
			case StorageCraftGUI.SOLDERER:
				return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile), (TileSolderer) tile);
			case StorageCraftGUI.WIRELESS_TRANSMITTER:
				return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile), (TileWirelessTransmitter) tile);
			case StorageCraftGUI.DESTRUCTOR:
				return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile), (TileDestructor) tile);
			case StorageCraftGUI.CONSTRUCTOR:
				return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile), (TileConstructor) tile);
			case StorageCraftGUI.STORAGE:
				return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (IStorageGui) tile);
			default:
				return null;
		}
	}
}
