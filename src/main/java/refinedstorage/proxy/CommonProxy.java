package refinedstorage.proxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.*;
import refinedstorage.gui.GuiHandler;
import refinedstorage.item.*;
import refinedstorage.network.*;
import refinedstorage.storage.NBTStorage;
import refinedstorage.tile.*;
import refinedstorage.tile.solderer.*;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent e)
	{
		RefinedStorage.NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);
		RefinedStorage.NETWORK.registerMessage(MessageRedstoneModeUpdate.class, MessageRedstoneModeUpdate.class, 1, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageStoragePush.class, MessageStoragePush.class, 2, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageStoragePull.class, MessageStoragePull.class, 3, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageCompareUpdate.class, MessageCompareUpdate.class, 4, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageModeToggle.class, MessageModeToggle.class, 5, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageDetectorModeUpdate.class, MessageDetectorModeUpdate.class, 6, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageDetectorAmountUpdate.class, MessageDetectorAmountUpdate.class, 7, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageGridCraftingUpdate.class, MessageGridCraftingUpdate.class, 8, Side.CLIENT);
		RefinedStorage.NETWORK.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, 9, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessagePriorityUpdate.class, MessagePriorityUpdate.class, 10, Side.SERVER);
		RefinedStorage.NETWORK.registerMessage(MessageGridSortingUpdate.class, MessageGridSortingUpdate.class, 11, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(RefinedStorage.INSTANCE, new GuiHandler());

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
		GameRegistry.registerTileEntity(TileStorage.class, "storage");
		GameRegistry.registerTileEntity(TileRelay.class, "relay");
		GameRegistry.registerTileEntity(TileInterface.class, "interface");

		GameRegistry.registerBlock(RefinedStorageBlocks.CONTROLLER, ItemBlockController.class, "controller");
		GameRegistry.registerBlock(RefinedStorageBlocks.CABLE, "cable");
		GameRegistry.registerBlock(RefinedStorageBlocks.GRID, ItemBlockGrid.class, "grid");
		GameRegistry.registerBlock(RefinedStorageBlocks.DRIVE, "drive");
		GameRegistry.registerBlock(RefinedStorageBlocks.EXTERNAL_STORAGE, "external_storage");
		GameRegistry.registerBlock(RefinedStorageBlocks.IMPORTER, "importer");
		GameRegistry.registerBlock(RefinedStorageBlocks.EXPORTER, "exporter");
		GameRegistry.registerBlock(RefinedStorageBlocks.DETECTOR, "detector");
		GameRegistry.registerBlock(RefinedStorageBlocks.MACHINE_CASING, "machine_casing");
		GameRegistry.registerBlock(RefinedStorageBlocks.SOLDERER, "solderer");
		GameRegistry.registerBlock(RefinedStorageBlocks.WIRELESS_TRANSMITTER, "wireless_transmitter");
		GameRegistry.registerBlock(RefinedStorageBlocks.DESTRUCTOR, "destructor");
		GameRegistry.registerBlock(RefinedStorageBlocks.CONSTRUCTOR, "constructor");
		GameRegistry.registerBlock(RefinedStorageBlocks.STORAGE, ItemBlockStorage.class, "storage");
		GameRegistry.registerBlock(RefinedStorageBlocks.RELAY, "relay"); // @TODO: Recipe
		GameRegistry.registerBlock(RefinedStorageBlocks.INTERFACE, "interface"); // @TODO: Recipe

		GameRegistry.registerItem(RefinedStorageItems.STORAGE_CELL, "storage_cell");
		GameRegistry.registerItem(RefinedStorageItems.WIRELESS_GRID, "wireless_grid");
		GameRegistry.registerItem(RefinedStorageItems.WIRELESS_GRID_PLATE, "wireless_grid_plate");
		GameRegistry.registerItem(RefinedStorageItems.QUARTZ_ENRICHED_IRON, "quartz_enriched_iron");
		GameRegistry.registerItem(RefinedStorageItems.CORE, "core");
		GameRegistry.registerItem(RefinedStorageItems.SILICON, "silicon");
		GameRegistry.registerItem(RefinedStorageItems.PROCESSOR, "processor");
		GameRegistry.registerItem(RefinedStorageItems.STORAGE_PART, "storage_part");

		// Processors
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED));
		SoldererRegistry.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON));

		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
		SoldererRegistry.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

		// Silicon
		GameRegistry.addSmelting(Items.quartz, new ItemStack(RefinedStorageItems.SILICON), 0.5f);

		// Quartz Enriched Iron
		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON, 4),
			"II",
			"IQ",
			'I', new ItemStack(Items.iron_ingot),
			'Q', new ItemStack(Items.quartz)
		);

		// Machine Casing
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			"EEE",
			"E E",
			"EEE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		// Construction Core
		GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			new ItemStack(Items.glowstone_dust)
		);

		// Destruction Core
		GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			new ItemStack(Items.quartz)
		);

		// Controller
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CONTROLLER, 1, EnumControllerType.NORMAL.getId()),
			"EDE",
			"SRS",
			"ESE",
			'D', new ItemStack(Items.diamond),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'R', new ItemStack(Items.redstone),
			'S', new ItemStack(RefinedStorageItems.SILICON)
		);

		// Solderer
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.SOLDERER),
			"ESE",
			"E E",
			"ESE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'S', new ItemStack(Blocks.sticky_piston)
		);

		// Drive
		SoldererRegistry.addRecipe(new SoldererRecipeDrive());

		// Cable
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CABLE, 6),
			"EEE",
			"GRG",
			"EEE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone)
		);

		// Grid
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
			"ECE",
			"PMP",
			"EDE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
			'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING)
		);

		// Crafting Grid
		SoldererRegistry.addRecipe(new SoldererRecipeCraftingGrid());

		// Wireless Transmitter
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.WIRELESS_TRANSMITTER),
			"EPE",
			"EME",
			"EAE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'A', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
			'P', new ItemStack(Items.ender_pearl),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING)
		);

		// Wireless Grid Plate
		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.WIRELESS_GRID_PLATE),
			" P ",
			"ERE",
			"EEE",
			'P', new ItemStack(Items.ender_pearl),
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		// Wireless Grid
		SoldererRegistry.addRecipe(new SoldererRecipeWirelessGrid(0));
		SoldererRegistry.addRecipe(new SoldererRecipeWirelessGrid(1));

		// External Storage
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.EXTERNAL_STORAGE),
			"CED",
			"HMH",
			"EPE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'H', new ItemStack(Blocks.chest),
			'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Importer
		GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.IMPORTER),
			new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC)
		);

		// Exporter
		GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.EXPORTER),
			new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC)
		);

		// Destructor
		GameRegistry.addShapedRecipe(new ItemStack(RefinedStorageBlocks.DESTRUCTOR),
			"EDE",
			"RMR",
			"EIE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
			'R', new ItemStack(Items.redstone),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			'I', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Constructor
		GameRegistry.addShapedRecipe(new ItemStack(RefinedStorageBlocks.CONSTRUCTOR),
			"ECE",
			"RMR",
			"EIE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
			'R', new ItemStack(Items.redstone),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			'I', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Detector
		GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.DETECTOR),
			"ECE",
			"RMR",
			"EPE",
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'R', new ItemStack(Items.redstone),
			'C', new ItemStack(Items.comparator),
			'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
		);

		// Storage Cell Parts
		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(RefinedStorageItems.SILICON),
			'S', new ItemStack(Blocks.glass)
		);

		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
			'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K)
		);

		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
			'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K)
		);

		GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
			"EPE",
			"SRS",
			"ESE",
			'R', new ItemStack(Items.redstone),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
			'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
			'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K)
		);

		// Storage Cells
		GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_1K)),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_4K)),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_16K)),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_64K)),
			"GRG",
			"RPR",
			"EEE",
			'G', new ItemStack(Blocks.glass),
			'R', new ItemStack(Items.redstone),
			'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
			'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
		);

		// Storage Blocks
		SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
		SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
		SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
		SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));
	}

	public void init(FMLInitializationEvent e)
	{
	}

	public void postInit(FMLPostInitializationEvent e)
	{
	}
}
