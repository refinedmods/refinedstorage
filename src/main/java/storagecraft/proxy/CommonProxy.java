package storagecraft.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import storagecraft.SC;
import storagecraft.SCBlocks;
import storagecraft.gui.GuiHandler;
import storagecraft.network.MessagePushToStorage;
import storagecraft.network.MessageTileUpdate;
import storagecraft.tile.TileCable;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		SC.NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);
		SC.NETWORK.registerMessage(MessagePushToStorage.class, MessagePushToStorage.class, 1, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(SC.INSTANCE, new GuiHandler());

		GameRegistry.registerTileEntity(TileController.class, "controller");
		GameRegistry.registerTileEntity(TileCable.class, "cable");
		GameRegistry.registerTileEntity(TileGrid.class, "grid");

		GameRegistry.registerBlock(SCBlocks.CONTROLLER, "controller");
		GameRegistry.registerBlock(SCBlocks.CABLE, "cable");
		GameRegistry.registerBlock(SCBlocks.GRID, "grid");
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
	}
}
