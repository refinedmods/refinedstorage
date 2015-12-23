package storagecraft.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.gui.GuiHandler;
import storagecraft.item.ItemBlockCable;
import storagecraft.item.ItemBlockGrid;
import storagecraft.item.ItemCore;
import storagecraft.network.MessageCompareUpdate;
import storagecraft.network.MessageDetectorAmountUpdate;
import storagecraft.network.MessageDetectorModeUpdate;
import storagecraft.network.MessageGridCraftingClear;
import storagecraft.network.MessageGridCraftingUpdate;
import storagecraft.network.MessageImporterModeUpdate;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.network.MessageTileUpdate;
import storagecraft.tile.TileCable;
import storagecraft.tile.TileController;
import storagecraft.tile.TileDetector;
import storagecraft.tile.TileDrive;
import storagecraft.tile.TileExporter;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileImporter;
import storagecraft.tile.TileStorageProxy;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent e)
	{
		StorageCraft.NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);
		StorageCraft.NETWORK.registerMessage(MessageRedstoneModeUpdate.class, MessageRedstoneModeUpdate.class, 1, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageStoragePush.class, MessageStoragePush.class, 2, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageStoragePull.class, MessageStoragePull.class, 3, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageCompareUpdate.class, MessageCompareUpdate.class, 4, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageImporterModeUpdate.class, MessageImporterModeUpdate.class, 5, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageDetectorModeUpdate.class, MessageDetectorModeUpdate.class, 6, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageDetectorAmountUpdate.class, MessageDetectorAmountUpdate.class, 7, Side.SERVER);
		StorageCraft.NETWORK.registerMessage(MessageGridCraftingUpdate.class, MessageGridCraftingUpdate.class, 8, Side.CLIENT);
		StorageCraft.NETWORK.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, 9, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(StorageCraft.INSTANCE, new GuiHandler());

		GameRegistry.registerTileEntity(TileController.class, "controller");
		GameRegistry.registerTileEntity(TileCable.class, "cable");
		GameRegistry.registerTileEntity(TileGrid.class, "grid");
		GameRegistry.registerTileEntity(TileDrive.class, "drive");
		GameRegistry.registerTileEntity(TileStorageProxy.class, "storageProxy");
		GameRegistry.registerTileEntity(TileImporter.class, "importer");
		GameRegistry.registerTileEntity(TileExporter.class, "exporter");
		GameRegistry.registerTileEntity(TileDetector.class, "detector");

		GameRegistry.registerBlock(StorageCraftBlocks.CONTROLLER, "controller");
		GameRegistry.registerBlock(StorageCraftBlocks.CABLE, ItemBlockCable.class, "cable");
		GameRegistry.registerBlock(StorageCraftBlocks.GRID, ItemBlockGrid.class, "grid");
		GameRegistry.registerBlock(StorageCraftBlocks.DRIVE, "drive");
		GameRegistry.registerBlock(StorageCraftBlocks.STORAGE_PROXY, "storageProxy");
		GameRegistry.registerBlock(StorageCraftBlocks.IMPORTER, "importer");
		GameRegistry.registerBlock(StorageCraftBlocks.EXPORTER, "exporter");
		GameRegistry.registerBlock(StorageCraftBlocks.DETECTOR, "detector");
		GameRegistry.registerBlock(StorageCraftBlocks.MACHINE_CASING, "machineCasing");

		GameRegistry.registerItem(StorageCraftItems.STORAGE_CELL, "storageCell");
		GameRegistry.registerItem(StorageCraftItems.WIRELESS_GRID, "wirelessGrid");
		GameRegistry.registerItem(StorageCraftItems.STORIGIUM_INGOT, "storigiumIngot");
		GameRegistry.registerItem(StorageCraftItems.CORE, "core");

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORIGIUM_INGOT, 4),
			"II",
			"IQ",
			'I', new ItemStack(Items.iron_ingot),
			'Q', new ItemStack(Items.quartz)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			"SSS",
			"S S",
			"SSS",
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			new ItemStack(Items.gold_ingot),
			new ItemStack(Items.glowstone_dust)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			new ItemStack(Items.quartz),
			new ItemStack(Items.glowstone_dust)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.CONTROLLER),
			"SDS",
			"DRD",
			"SDS",
			'D', new ItemStack(Items.diamond),
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			'R', new ItemStack(Blocks.redstone_block)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.DRIVE),
			new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			new ItemStack(Blocks.chest)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.CABLE, 6, 0),
			"SSS",
			"GGG",
			"SSS",
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			'G', new ItemStack(Blocks.glass)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.CABLE, 1, 1),
			new ItemStack(StorageCraftBlocks.CABLE, 1, 0),
			new ItemStack(Items.redstone)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.GRID, 1, 0),
			"SCS",
			"GMG",
			"SDS",
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			'G', new ItemStack(Blocks.glass),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.GRID, 1, 1),
			new ItemStack(StorageCraftBlocks.GRID, 1, 0),
			new ItemStack(Blocks.crafting_table)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.WIRELESS_GRID),
			"PCP",
			"PGP",
			"PDP",
			'P', new ItemStack(Items.ender_pearl),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'G', new ItemStack(Blocks.glass)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.STORAGE_PROXY),
			"SCS",
			"HMH",
			"SDS",
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			'H', new ItemStack(Blocks.chest),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.IMPORTER),
			new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.EXPORTER),
			new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.DETECTOR),
			"SCS",
			"RMR",
			"SRS",
			'S', new ItemStack(StorageCraftItems.STORIGIUM_INGOT),
			'R', new ItemStack(Items.redstone),
			'C', new ItemStack(Items.comparator),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING)
		);

		// @TODO: Recipe for storage cells
	}

	public void init(FMLInitializationEvent e)
	{
	}

	public void postInit(FMLPostInitializationEvent e)
	{
	}
}
