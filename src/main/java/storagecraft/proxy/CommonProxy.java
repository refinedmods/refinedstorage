package storagecraft.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.gui.GuiHandler;
import storagecraft.item.ItemBlockCable;
import storagecraft.network.MessageExporterUpdate;
import storagecraft.network.MessageImporterUpdate;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.network.MessageTileUpdate;
import storagecraft.tile.TileCable;
import storagecraft.tile.TileController;
import storagecraft.tile.TileDrive;
import storagecraft.tile.TileExporter;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileImporter;
import storagecraft.tile.TileStorageProxy;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		StorageCraft.NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);
		StorageCraft.NETWORK.registerMessage(MessageRedstoneModeUpdate.class, MessageRedstoneModeUpdate.class, 1, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageStoragePush.class, MessageStoragePush.class, 2, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageStoragePull.class, MessageStoragePull.class, 3, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageImporterUpdate.class, MessageImporterUpdate.class, 4, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageExporterUpdate.class, MessageExporterUpdate.class, 5, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(StorageCraft.INSTANCE, new GuiHandler());

		GameRegistry.registerTileEntity(TileController.class, "controller");
		GameRegistry.registerTileEntity(TileCable.class, "cable");
		GameRegistry.registerTileEntity(TileGrid.class, "grid");
		GameRegistry.registerTileEntity(TileDrive.class, "drive");
		GameRegistry.registerTileEntity(TileStorageProxy.class, "storageProxy");
		GameRegistry.registerTileEntity(TileImporter.class, "importer");
		GameRegistry.registerTileEntity(TileExporter.class, "exporter");

		GameRegistry.registerBlock(StorageCraftBlocks.CONTROLLER, "controller");
		GameRegistry.registerBlock(StorageCraftBlocks.CABLE, ItemBlockCable.class, "cable");
		GameRegistry.registerBlock(StorageCraftBlocks.GRID, "grid");
		GameRegistry.registerBlock(StorageCraftBlocks.DRIVE, "drive");
		GameRegistry.registerBlock(StorageCraftBlocks.STORAGE_PROXY, "storageProxy");
		GameRegistry.registerBlock(StorageCraftBlocks.IMPORTER, "importer");
		GameRegistry.registerBlock(StorageCraftBlocks.EXPORTER, "exporter");

		GameRegistry.registerItem(StorageCraftItems.STORAGE_CELL, "storageCell");
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
	}
}
