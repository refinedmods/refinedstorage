package storagecraft.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.SC;
import storagecraft.container.ContainerController;
import storagecraft.container.ContainerDrive;
import storagecraft.container.ContainerGrid;
import storagecraft.container.ContainerImporter;
import storagecraft.tile.TileController;
import storagecraft.tile.TileDrive;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileImporter;

public class GuiHandler implements IGuiHandler {
	private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
		switch (ID) {
			case SC.GUI.CONTROLLER:
				return new ContainerController(player);
			case SC.GUI.GRID:
				return new ContainerGrid(player);
			case SC.GUI.DRIVE:
				return new ContainerDrive(player, (TileDrive) tile);
			case SC.GUI.IMPORTER:
				return new ContainerImporter(player, (TileImporter) tile);
			default:
				return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return getContainer(ID, player, world.getTileEntity(x, y, z));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);

		switch (ID) {
			case SC.GUI.CONTROLLER:
				return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
			case SC.GUI.GRID:
				return new GuiGrid((ContainerGrid) getContainer(ID, player, tile), (TileGrid) tile);
			case SC.GUI.DRIVE:
				return new GuiDrive((ContainerDrive) getContainer(ID, player, tile));
			case SC.GUI.IMPORTER:
				return new GuiImporter((ContainerImporter) getContainer(ID, player, tile), (TileImporter) tile);
			default:
				return null;
		}
	}
}
