package refinedstorage.proxy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.apiimpl.solderer.*;
import refinedstorage.apiimpl.storage.NBTStorage;
import refinedstorage.block.BlockBase;
import refinedstorage.block.EnumControllerType;
import refinedstorage.block.EnumGridType;
import refinedstorage.block.EnumStorageType;
import refinedstorage.gui.GuiHandler;
import refinedstorage.item.*;
import refinedstorage.network.*;
import refinedstorage.tile.*;
import refinedstorage.tile.controller.TileController;
import refinedstorage.tile.externalstorage.TileExternalStorage;
import refinedstorage.tile.grid.TileGrid;

import static refinedstorage.RefinedStorage.ID;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        RefinedStorageAPI.SOLDERER_REGISTRY = new SoldererRegistry();

        int id = 0;

        RefinedStorage.INSTANCE.network.registerMessage(MessageTileContainerUpdate.class, MessageTileContainerUpdate.class, id++, Side.CLIENT);
        RefinedStorage.INSTANCE.network.registerMessage(MessageRedstoneModeUpdate.class, MessageRedstoneModeUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridInsertHeld.class, MessageGridInsertHeld.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridPull.class, MessageGridPull.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageCompareUpdate.class, MessageCompareUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageModeToggle.class, MessageModeToggle.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageDetectorModeUpdate.class, MessageDetectorModeUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageDetectorAmountUpdate.class, MessageDetectorAmountUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessagePriorityUpdate.class, MessagePriorityUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridSettingsUpdate.class, MessageGridSettingsUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridCraftingTransfer.class, MessageGridCraftingTransfer.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageWirelessGridSettingsUpdate.class, MessageWirelessGridSettingsUpdate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridCraftingStart.class, MessageGridCraftingStart.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridPatternCreate.class, MessageGridPatternCreate.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageCraftingMonitorCancel.class, MessageCraftingMonitorCancel.class, id++, Side.SERVER);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridUpdate.class, MessageGridUpdate.class, id++, Side.CLIENT);
        RefinedStorage.INSTANCE.network.registerMessage(MessageGridDelta.class, MessageGridDelta.class, id++, Side.CLIENT);

        NetworkRegistry.INSTANCE.registerGuiHandler(RefinedStorage.INSTANCE, new GuiHandler());

        GameRegistry.registerTileEntity(TileController.class, ID + ":controller");
        GameRegistry.registerTileEntity(TileGrid.class, ID + ":grid");
        GameRegistry.registerTileEntity(TileDiskDrive.class, ID + ":disk_drive");
        GameRegistry.registerTileEntity(TileExternalStorage.class, ID + ":external_storage");
        GameRegistry.registerTileEntity(TileImporter.class, ID + ":importer");
        GameRegistry.registerTileEntity(TileExporter.class, ID + ":exporter");
        GameRegistry.registerTileEntity(TileDetector.class, ID + ":detector");
        GameRegistry.registerTileEntity(TileSolderer.class, ID + ":solderer");
        GameRegistry.registerTileEntity(TileDestructor.class, ID + ":destructor");
        GameRegistry.registerTileEntity(TileConstructor.class, ID + ":constructor");
        GameRegistry.registerTileEntity(TileStorage.class, ID + ":storage");
        GameRegistry.registerTileEntity(TileRelay.class, ID + ":relay");
        GameRegistry.registerTileEntity(TileInterface.class, ID + ":interface");
        GameRegistry.registerTileEntity(TileCraftingMonitor.class, ID + ":crafting_monitor");
        GameRegistry.registerTileEntity(TileWirelessTransmitter.class, ID + ":wireless_transmitter");
        GameRegistry.registerTileEntity(TileCrafter.class, ID + ":crafter");
        GameRegistry.registerTileEntity(TileProcessingPatternEncoder.class, ID + ":processing_pattern_encoder");
        GameRegistry.registerTileEntity(TileCable.class, ID + ":cable");

        registerBlock(RefinedStorageBlocks.CONTROLLER);
        registerBlock(RefinedStorageBlocks.GRID);
        registerBlock(RefinedStorageBlocks.CRAFTING_MONITOR);
        registerBlock(RefinedStorageBlocks.CRAFTER);
        registerBlock(RefinedStorageBlocks.PROCESSING_PATTERN_ENCODER);
        registerBlock(RefinedStorageBlocks.DISK_DRIVE);
        registerBlock(RefinedStorageBlocks.STORAGE);
        registerBlock(RefinedStorageBlocks.SOLDERER);
        registerBlock(RefinedStorageBlocks.CABLE);
        registerBlock(RefinedStorageBlocks.IMPORTER);
        registerBlock(RefinedStorageBlocks.EXPORTER);
        registerBlock(RefinedStorageBlocks.EXTERNAL_STORAGE);
        registerBlock(RefinedStorageBlocks.CONSTRUCTOR);
        registerBlock(RefinedStorageBlocks.DESTRUCTOR);
        registerBlock(RefinedStorageBlocks.DETECTOR);
        registerBlock(RefinedStorageBlocks.RELAY);
        registerBlock(RefinedStorageBlocks.INTERFACE);
        registerBlock(RefinedStorageBlocks.WIRELESS_TRANSMITTER);
        registerBlock(RefinedStorageBlocks.MACHINE_CASING);

        registerItem(RefinedStorageItems.QUARTZ_ENRICHED_IRON);
        registerItem(RefinedStorageItems.STORAGE_DISK);
        registerItem(RefinedStorageItems.STORAGE_HOUSING);
        registerItem(RefinedStorageItems.PATTERN);
        registerItem(RefinedStorageItems.STORAGE_PART);
        registerItem(RefinedStorageItems.WIRELESS_GRID);
        registerItem(RefinedStorageItems.PROCESSOR);
        registerItem(RefinedStorageItems.CORE);
        registerItem(RefinedStorageItems.SILICON);
        registerItem(RefinedStorageItems.UPGRADE);

        OreDictionary.registerOre("itemSilicon", RefinedStorageItems.SILICON);

        // Processors
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON));

        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

        // Silicon
        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(RefinedStorageItems.SILICON), 0.5f);

        // Quartz Enriched Iron
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON, 4),
            "II",
            "IQ",
            'I', new ItemStack(Items.IRON_INGOT),
            'Q', new ItemStack(Items.QUARTZ)
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
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.GLOWSTONE_DUST)
        );

        // Destruction Core
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.QUARTZ)
        );

        // Relay
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.RELAY),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            new ItemStack(RefinedStorageBlocks.CABLE)
        );

        // Controller
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RefinedStorageBlocks.CONTROLLER, 1, EnumControllerType.NORMAL.getId()),
            "EDE",
            "SMS",
            "ESE",
            'D', new ItemStack(Items.DIAMOND),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'S', "itemSilicon"
        ));

        // Solderer
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.SOLDERER),
            "ESE",
            "E E",
            "ESE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'S', new ItemStack(Blocks.STICKY_PISTON)
        );

        // Disk Drive
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeBasic(
            new ItemStack(RefinedStorageBlocks.DISK_DRIVE),
            500,
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            new ItemStack(Blocks.CHEST)
        ));

        // Cable
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CABLE, 12),
            "EEE",
            "GRG",
            "EEE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'G', new ItemStack(Blocks.GLASS),
            'R', new ItemStack(Items.REDSTONE)
        );

        // Wireless Transmitter
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.WIRELESS_TRANSMITTER),
            "EPE",
            "EME",
            "EAE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'P', new ItemStack(Items.ENDER_PEARL),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING)
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
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeBasic(
            new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.CRAFTING.getId()),
            500,
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(Blocks.CRAFTING_TABLE)
        ));

        // Pattern Grid
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeBasic(
            new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.PATTERN.getId()),
            500,
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(RefinedStorageItems.PATTERN)
        ));

        // Wireless Grid
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.WIRELESS_GRID, 1, ItemWirelessGrid.TYPE_NORMAL),
            "EPE",
            "EAE",
            "EEE",
            'P', new ItemStack(Items.ENDER_PEARL),
            'A', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Crafter
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CRAFTER),
            "ECE",
            "AMA",
            "EDE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING)
        );

        // Processing Pattern Encoder
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.PROCESSING_PATTERN_ENCODER),
            "ECE",
            "PMP",
            "EFE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'P', new ItemStack(RefinedStorageItems.PATTERN),
            'C', new ItemStack(Blocks.CRAFTING_TABLE),
            'F', new ItemStack(Blocks.FURNACE)
        );

        // External Storage
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.EXTERNAL_STORAGE),
            "CED",
            "HMH",
            "EPE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'H', new ItemStack(Blocks.CHEST),
            'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RefinedStorageBlocks.CABLE),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Importer
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.IMPORTER),
            new ItemStack(RefinedStorageBlocks.CABLE),
            new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Exporter
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.EXPORTER),
            new ItemStack(RefinedStorageBlocks.CABLE),
            new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Destructor
        GameRegistry.addShapedRecipe(new ItemStack(RefinedStorageBlocks.DESTRUCTOR),
            "EDE",
            "RMR",
            "EIE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
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
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'I', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Detector
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.DETECTOR),
            "ECE",
            "RMR",
            "EPE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'R', new ItemStack(Items.REDSTONE),
            'C', new ItemStack(Items.COMPARATOR),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Storage Parts
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
            "SES",
            "GRG",
            "SGS",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'S', "itemSilicon",
            'G', new ItemStack(Blocks.GLASS)
        ));

        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K)
        );

        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K)
        );

        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'S', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K)
        );

        // Storage Housing
        GameRegistry.addRecipe(NBTStorage.createStackWithNBT(new ItemStack(RefinedStorageItems.STORAGE_HOUSING)),
            "GRG",
            "R R",
            "EEE",
            'G', new ItemStack(Blocks.GLASS),
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = NBTStorage.createStackWithNBT(new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, type));

            GameRegistry.addRecipe(disk,
                "GRG",
                "RPR",
                "EEE",
                'G', new ItemStack(Blocks.GLASS),
                'R', new ItemStack(Items.REDSTONE),
                'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, type),
                'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
            );

            GameRegistry.addShapelessRecipe(disk,
                new ItemStack(RefinedStorageItems.STORAGE_HOUSING),
                new ItemStack(RefinedStorageItems.STORAGE_PART, 1, type)
            );
        }

        // Pattern
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.PATTERN),
            "GRG",
            "RGR",
            "EEE",
            'G', new ItemStack(Blocks.GLASS),
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Upgrade
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.UPGRADE, 1, 0),
            "EGE",
            "EPE",
            "EGE",
            'G', new ItemStack(Blocks.GLASS),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_RANGE));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SPEED));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_CRAFTING));

        GameRegistry.addShapedRecipe(new ItemStack(RefinedStorageItems.UPGRADE, 1, ItemUpgrade.TYPE_STACK),
            "USU",
            "SUS",
            "USU",
            'U', new ItemStack(Items.SUGAR),
            'S', new ItemStack(RefinedStorageItems.UPGRADE, 1, ItemUpgrade.TYPE_SPEED)
        );

        // Storage Blocks
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Crafting Monitor
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CRAFTING_MONITOR),
            "EGE",
            "GMG",
            "EPE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'G', new ItemStack(Blocks.GLASS),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Interface
        RefinedStorageAPI.SOLDERER_REGISTRY.addRecipe(new SoldererRecipeBasic(
            new ItemStack(RefinedStorageBlocks.INTERFACE),
            200,
            new ItemStack(RefinedStorageBlocks.IMPORTER),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RefinedStorageBlocks.EXPORTER)
        ));
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerBlock(BlockBase block) {
        GameRegistry.<Block>register(block);
        GameRegistry.register(block.createItem());
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }
}
