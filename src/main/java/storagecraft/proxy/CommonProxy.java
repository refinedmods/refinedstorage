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
import storagecraft.item.ItemProcessor;
import storagecraft.item.ItemStorageCell;
import storagecraft.item.ItemStoragePart;
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
import storagecraft.tile.TileSolderer;
import storagecraft.tile.TileExternalStorage;
import storagecraft.tile.TileWirelessTransmitter;
import storagecraft.tile.solderer.SoldererRecipeCraftingGrid;
import storagecraft.tile.solderer.SoldererRecipeDrive;
import storagecraft.tile.solderer.SoldererRecipePrintedProcessor;
import storagecraft.tile.solderer.SoldererRecipeProcessor;
import storagecraft.tile.solderer.SoldererRecipeWirelessGrid;
import storagecraft.tile.solderer.SoldererRegistry;

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
		GameRegistry.registerTileEntity(TileExternalStorage.class, "externalStorage");
		GameRegistry.registerTileEntity(TileImporter.class, "importer");
		GameRegistry.registerTileEntity(TileExporter.class, "exporter");
		GameRegistry.registerTileEntity(TileDetector.class, "detector");
		GameRegistry.registerTileEntity(TileSolderer.class, "solderer");
		GameRegistry.registerTileEntity(TileWirelessTransmitter.class, "wirelessTransmitter");

		GameRegistry.registerBlock(StorageCraftBlocks.CONTROLLER, "controller");
		GameRegistry.registerBlock(StorageCraftBlocks.CABLE, ItemBlockCable.class, "cable");
		GameRegistry.registerBlock(StorageCraftBlocks.GRID, ItemBlockGrid.class, "grid");
		GameRegistry.registerBlock(StorageCraftBlocks.DRIVE, "drive");
		GameRegistry.registerBlock(StorageCraftBlocks.EXTERNAL_STORAGE, "externalStorage");
		GameRegistry.registerBlock(StorageCraftBlocks.IMPORTER, "importer");
		GameRegistry.registerBlock(StorageCraftBlocks.EXPORTER, "exporter");
		GameRegistry.registerBlock(StorageCraftBlocks.DETECTOR, "detector");
		GameRegistry.registerBlock(StorageCraftBlocks.MACHINE_CASING, "machineCasing");
		GameRegistry.registerBlock(StorageCraftBlocks.SOLDERER, "solderer");
		GameRegistry.registerBlock(StorageCraftBlocks.WIRELESS_TRANSMITTER, "wirelessTransmitter");

		GameRegistry.registerItem(StorageCraftItems.STORAGE_CELL, "storageCell");
		GameRegistry.registerItem(StorageCraftItems.WIRELESS_GRID, "wirelessGrid");
		GameRegistry.registerItem(StorageCraftItems.QUARTZ_ENRICHED_IRON, "storigiumIngot");
		GameRegistry.registerItem(StorageCraftItems.CORE, "core");
		GameRegistry.registerItem(StorageCraftItems.SILICON, "silicon");
		GameRegistry.registerItem(StorageCraftItems.PROCESSOR, "processor");
		GameRegistry.registerItem(StorageCraftItems.STORAGE_PART, "storagePart");
		GameRegistry.registerItem(StorageCraftItems.WIRELESS_GRID_PLATE, "wirelessGridPlate");

		// Processors
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON));

		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

		// Silicon
		GameRegistry.addSmelting(Items.quartz, new ItemStack(StorageCraftItems.SILICON), 0.5f);

		// Quartz Enriched Iron
		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON, 4),
			"II",
			"IQ",
			'I', new ItemStack(Items.iron_ingot),
			'Q', new ItemStack(Items.quartz)
		);

		// Machine Casing
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			"EEE",
			"E E",
			"EEE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

		// Construction Core
		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			new ItemStack(Items.glowstone_dust)
		);

		// Destruction Core
		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			new ItemStack(Items.quartz)
		);

		// Constroller
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.CONTROLLER),
			"EDE",
			"SRS",
			"ESE",
			'D', new ItemStack(Items.diamond),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'R', new ItemStack(Items.redstone),
			'S', new ItemStack(StorageCraftItems.SILICON)
		);

		// Solderer
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.SOLDERER),
			"ESE",
			"E E",
			"ESE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'S', new ItemStack(Blocks.sticky_piston)
		);

		// Drive
		SoldererRegistry.addRecipe(new SoldererRecipeDrive());

		// Cable
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.CABLE, 6, 0),
			"EEE",
			"GRG",
			"EEE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone)
		);

		// Sensitive Cable
		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.CABLE, 1, 1),
			new ItemStack(StorageCraftBlocks.CABLE, 1, 0),
			new ItemStack(Items.redstone)
		);

		// Grid
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.GRID, 1, 0),
			"ECE",
			"PMP",
			"EDE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING)
		);

		// Crafting Grid
		SoldererRegistry.addRecipe(new SoldererRecipeCraftingGrid());

		// Wireless Grid Plate
		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.WIRELESS_GRID_PLATE),
			" P ",
			"ERE",
			"EEE",
			'P', new ItemStack(Items.ender_pearl),
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

		// @TODO: Wireless Transmitter
		// Wireless Grid
		SoldererRegistry.addRecipe(new SoldererRecipeWirelessGrid(0));
		SoldererRegistry.addRecipe(new SoldererRecipeWirelessGrid(1));

		// External Storage
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.EXTERNAL_STORAGE),
			"CED",
			"HMH",
			"EPE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'H', new ItemStack(Blocks.chest),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Importer
		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.IMPORTER),
			new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC)
		);

		// Exporter
		GameRegistry.addShapelessRecipe(new ItemStack(StorageCraftBlocks.EXPORTER),
			new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC)
		);

		// Detector
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.DETECTOR),
			"ECE",
			"RMR",
			"EPE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'R', new ItemStack(Items.redstone),
			'C', new ItemStack(Items.comparator),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Storage Cell Parts
		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(StorageCraftItems.SILICON),
			'S', new ItemStack(Blocks.glass)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			'S', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
			'S', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
			'S', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K)
		);

		// Storage Cells
		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_1K),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_4K),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_16K),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_64K),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(StorageCraftItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);
	}

	public void init(FMLInitializationEvent e)
	{
	}

	public void postInit(FMLPostInitializationEvent e)
	{
	}
}
