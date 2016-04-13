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
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.*;
import refinedstorage.gui.GuiHandler;
import refinedstorage.item.*;
import refinedstorage.network.*;
import refinedstorage.storage.NBTStorage;
import refinedstorage.tile.*;
import refinedstorage.tile.autocrafting.TileCraftingCPU;
import refinedstorage.tile.autocrafting.TileCraftingMonitor;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.solderer.*;

import static refinedstorage.RefinedStorage.ID;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        RefinedStorage.NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);
        RefinedStorage.NETWORK.registerMessage(MessageTileContainerUpdate.class, MessageTileContainerUpdate.class, 15, Side.CLIENT);
        RefinedStorage.NETWORK.registerMessage(MessageRedstoneModeUpdate.class, MessageRedstoneModeUpdate.class, 1, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridStoragePush.class, MessageGridStoragePush.class, 2, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridStoragePull.class, MessageGridStoragePull.class, 3, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageCompareUpdate.class, MessageCompareUpdate.class, 4, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageModeToggle.class, MessageModeToggle.class, 5, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageDetectorModeUpdate.class, MessageDetectorModeUpdate.class, 6, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageDetectorAmountUpdate.class, MessageDetectorAmountUpdate.class, 7, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, 9, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessagePriorityUpdate.class, MessagePriorityUpdate.class, 10, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridSettingsUpdate.class, MessageGridSettingsUpdate.class, 11, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridCraftingPush.class, MessageGridCraftingPush.class, 12, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridCraftingTransfer.class, MessageGridCraftingTransfer.class, 13, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageWirelessGridSettingsUpdate.class, MessageWirelessGridSettingsUpdate.class, 14, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageWirelessGridItems.class, MessageWirelessGridItems.class, 16, Side.CLIENT);
        RefinedStorage.NETWORK.registerMessage(MessageWirelessGridStoragePush.class, MessageWirelessGridStoragePush.class, 17, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageWirelessGridStoragePull.class, MessageWirelessGridStoragePull.class, 18, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridCraftingShift.class, MessageGridCraftingShift.class, 19, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridCraftingStart.class, MessageGridCraftingStart.class, 20, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageWirelessGridCraftingStart.class, MessageWirelessGridCraftingStart.class, 21, Side.SERVER);
        RefinedStorage.NETWORK.registerMessage(MessageGridPatternCreate.class, MessageGridPatternCreate.class, 22, Side.SERVER);

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
        GameRegistry.registerTileEntity(TileCraftingCPU.class, ID + ":crafting_cpu");
        GameRegistry.registerTileEntity(TileWirelessTransmitter.class, ID + ":wireless_transmitter");

        registerBlock(RefinedStorageBlocks.CONTROLLER);
        registerBlock(RefinedStorageBlocks.CABLE);
        registerBlock(RefinedStorageBlocks.GRID);
        registerBlock(RefinedStorageBlocks.DISK_DRIVE);
        registerBlock(RefinedStorageBlocks.EXTERNAL_STORAGE);
        registerBlock(RefinedStorageBlocks.IMPORTER);
        registerBlock(RefinedStorageBlocks.EXPORTER);
        registerBlock(RefinedStorageBlocks.DETECTOR);
        registerBlock(RefinedStorageBlocks.MACHINE_CASING);
        registerBlock(RefinedStorageBlocks.SOLDERER);
        registerBlock(RefinedStorageBlocks.DESTRUCTOR);
        registerBlock(RefinedStorageBlocks.CONSTRUCTOR);
        registerBlock(RefinedStorageBlocks.STORAGE);
        registerBlock(RefinedStorageBlocks.RELAY);
        registerBlock(RefinedStorageBlocks.INTERFACE);
        registerBlock(RefinedStorageBlocks.CRAFTING_MONITOR);
        registerBlock(RefinedStorageBlocks.CRAFTING_CPU);
        registerBlock(RefinedStorageBlocks.WIRELESS_TRANSMITTER);
        registerBlock(RefinedStorageBlocks.CRAFTING_UNIT);

        registerItem(RefinedStorageItems.STORAGE_DISK);
        registerItem(RefinedStorageItems.PATTERN);
        registerItem(RefinedStorageItems.WIRELESS_GRID);
        registerItem(RefinedStorageItems.QUARTZ_ENRICHED_IRON);
        registerItem(RefinedStorageItems.CORE);
        registerItem(RefinedStorageItems.SILICON);
        registerItem(RefinedStorageItems.PROCESSOR);
        registerItem(RefinedStorageItems.STORAGE_PART);
        registerItem(RefinedStorageItems.UPGRADE);

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
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.glowstone_dust)
        );

        // Destruction Core
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.quartz)
        );

        // Relay
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.RELAY),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            new ItemStack(RefinedStorageBlocks.CABLE)
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

        // Disk Drive
        SoldererRegistry.addRecipe(new SoldererRecipeDiskDrive());

        // Cable
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CABLE, 6),
            "EEE",
            "GRG",
            "EEE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone)
        );

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

        // Pattern Grid
        SoldererRegistry.addRecipe(new SoldererRecipePatternGrid());

        // Wireless Grid
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.WIRELESS_GRID, 1, ItemWirelessGrid.TYPE_NORMAL),
            " P ",
            "ERE",
            "EEE",
            'P', new ItemStack(Items.ender_pearl),
            'R', new ItemStack(Items.redstone),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

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
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Exporter
        GameRegistry.addShapelessRecipe(new ItemStack(RefinedStorageBlocks.EXPORTER),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
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

        // Storage Parts
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

        // Storage Disks
        GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_1K)),
            "GRG",
            "RPR",
            "EEE",
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone),
            'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_4K)),
            "GRG",
            "RPR",
            "EEE",
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone),
            'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_16K)),
            "GRG",
            "RPR",
            "EEE",
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone),
            'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        GameRegistry.addRecipe(NBTStorage.initNBT(new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_64K)),
            "GRG",
            "RPR",
            "EEE",
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone),
            'P', new ItemStack(RefinedStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Pattern
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.PATTERN),
            "GRG",
            "RGR",
            "EEE",
            'G', new ItemStack(Blocks.glass),
            'R', new ItemStack(Items.redstone),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Upgrade
        GameRegistry.addRecipe(new ItemStack(RefinedStorageItems.UPGRADE, 1, 0),
            "EGE",
            "EPE",
            "EGE",
            'G', new ItemStack(Blocks.glass),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON)
        );

        SoldererRegistry.addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_RANGE));
        SoldererRegistry.addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SPEED));

        // Storage Blocks
        SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
        SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
        SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
        SoldererRegistry.addRecipe(new SoldererRecipeStorage(EnumStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Crafting Unit
        GameRegistry.addShapedRecipe(new ItemStack(RefinedStorageBlocks.CRAFTING_UNIT),
            "ECE",
            "PMP",
            "EDE",
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'C', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RefinedStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION)
        );

        // Crafting CPUs
        SoldererRegistry.addRecipe(new SoldererRecipeCraftingCPU(EnumCraftingCPUType.TYPE_1K, ItemStoragePart.TYPE_1K));
        SoldererRegistry.addRecipe(new SoldererRecipeCraftingCPU(EnumCraftingCPUType.TYPE_4K, ItemStoragePart.TYPE_4K));
        SoldererRegistry.addRecipe(new SoldererRecipeCraftingCPU(EnumCraftingCPUType.TYPE_16K, ItemStoragePart.TYPE_16K));
        SoldererRegistry.addRecipe(new SoldererRecipeCraftingCPU(EnumCraftingCPUType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Crafting Monitor
        GameRegistry.addRecipe(new ItemStack(RefinedStorageBlocks.CRAFTING_MONITOR),
            "EGE",
            "GMG",
            "EPE",
            'E', new ItemStack(RefinedStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            'G', new ItemStack(Blocks.glass),
            'P', new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Interface
        SoldererRegistry.addRecipe(new SoldererRecipeInterface());
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerBlock(BlockBase block) {
        GameRegistry.<Block>register(block);
        GameRegistry.register(block.createItemForBlock());
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }
}
