package storagecraft.proxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.block.EnumGridType;
import storagecraft.gui.GuiHandler;
import storagecraft.item.*;
import storagecraft.network.*;
import storagecraft.tile.*;
import storagecraft.tile.solderer.*;
import storagecraft.block.EnumControllerType;

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
		GameRegistry.registerTileEntity(TileExternalStorage.class, "external_storage");
		GameRegistry.registerTileEntity(TileImporter.class, "importer");
		GameRegistry.registerTileEntity(TileExporter.class, "exporter");
		GameRegistry.registerTileEntity(TileDetector.class, "detector");
		GameRegistry.registerTileEntity(TileSolderer.class, "solderer");
		GameRegistry.registerTileEntity(TileWirelessTransmitter.class, "wireless_transmitter");
		GameRegistry.registerTileEntity(TileDestructor.class, "destructor");
		GameRegistry.registerTileEntity(TileConstructor.class, "constructor");

		GameRegistry.registerBlock(StorageCraftBlocks.CONTROLLER, ItemBlockController.class, "controller");
		GameRegistry.registerBlock(StorageCraftBlocks.CABLE, ItemBlockCable.class, "cable");
		GameRegistry.registerBlock(StorageCraftBlocks.GRID, ItemBlockGrid.class, "grid");
		GameRegistry.registerBlock(StorageCraftBlocks.DRIVE, "drive");
		GameRegistry.registerBlock(StorageCraftBlocks.EXTERNAL_STORAGE, "external_storage");
		GameRegistry.registerBlock(StorageCraftBlocks.IMPORTER, "importer");
		GameRegistry.registerBlock(StorageCraftBlocks.EXPORTER, "exporter");
		GameRegistry.registerBlock(StorageCraftBlocks.DETECTOR, "detector");
		GameRegistry.registerBlock(StorageCraftBlocks.MACHINE_CASING, "machine_casing");
		GameRegistry.registerBlock(StorageCraftBlocks.SOLDERER, "solderer");
		GameRegistry.registerBlock(StorageCraftBlocks.WIRELESS_TRANSMITTER, "wireless_transmitter");
		GameRegistry.registerBlock(StorageCraftBlocks.DESTRUCTOR, "destructor");
		GameRegistry.registerBlock(StorageCraftBlocks.CONSTRUCTOR, "constructor");

		GameRegistry.registerItem(StorageCraftItems.STORAGE_CELL, "storage_cell");
		GameRegistry.registerItem(StorageCraftItems.WIRELESS_GRID, "wireless_grid");
		GameRegistry.registerItem(StorageCraftItems.WIRELESS_GRID_PLATE, "wireless_grid_plate");
		GameRegistry.registerItem(StorageCraftItems.QUARTZ_ENRICHED_IRON, "quartz_enriched_iron");
		GameRegistry.registerItem(StorageCraftItems.CORE, "core");
		GameRegistry.registerItem(StorageCraftItems.SILICON, "silicon");
		GameRegistry.registerItem(StorageCraftItems.PROCESSOR, "processor");
		GameRegistry.registerItem(StorageCraftItems.STORAGE_PART, "storage_part");

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

		// Controller
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.CONTROLLER, 1, EnumControllerType.NORMAL.getId()),
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
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
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

		// Wireless Transmitter
		GameRegistry.addRecipe(new ItemStack(StorageCraftBlocks.WIRELESS_TRANSMITTER),
			"EPE",
			"EME",
			"EAE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'A', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
			'P', new ItemStack(Items.ender_pearl),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING)
		);

		// Wireless Grid Plate
		GameRegistry.addRecipe(new ItemStack(StorageCraftItems.WIRELESS_GRID_PLATE),
			" P ",
			"ERE",
			"EEE",
			'P', new ItemStack(Items.ender_pearl),
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON)
		);

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

		// Destructor
		GameRegistry.addShapedRecipe(new ItemStack(StorageCraftBlocks.DESTRUCTOR),
			"EDE",
			"RMR",
			"EIE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'D', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'R', new ItemStack(Items.redstone),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			'I', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Constructor
		GameRegistry.addShapedRecipe(new ItemStack(StorageCraftBlocks.CONSTRUCTOR),
			"ECE",
			"RMR",
			"EIE",
			'E', new ItemStack(StorageCraftItems.QUARTZ_ENRICHED_IRON),
			'C', new ItemStack(StorageCraftItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'R', new ItemStack(Items.redstone),
			'M', new ItemStack(StorageCraftBlocks.MACHINE_CASING),
			'I', new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
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
