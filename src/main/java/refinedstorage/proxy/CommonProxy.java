package refinedstorage.proxy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import refinedstorage.RS;
import refinedstorage.RSBlocks;
import refinedstorage.RSItems;
import refinedstorage.RSUtils;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import refinedstorage.apiimpl.solderer.*;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import refinedstorage.block.*;
import refinedstorage.gui.GuiHandler;
import refinedstorage.integration.craftingtweaks.IntegrationCraftingTweaks;
import refinedstorage.item.*;
import refinedstorage.network.*;
import refinedstorage.tile.*;
import refinedstorage.tile.data.ContainerListener;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.externalstorage.TileExternalStorage;
import refinedstorage.tile.grid.TileGrid;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {
    protected List<BlockCable> cableTypes = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent e) {
        if (IntegrationCraftingTweaks.isLoaded()) {
            IntegrationCraftingTweaks.register();
        }

        API.deliver(e.getAsmData());

        API.instance().getCraftingTaskRegistry().addFactory(CraftingTaskFactory.ID, new CraftingTaskFactory());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(buf.readInt(), ByteBufUtils.readItemStack(buf), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(buf.readInt(), RSUtils.readFluidStack(buf).getRight(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementText.ID, buf -> new CraftingMonitorElementText(ByteBufUtils.readUTF8String(buf), buf.readInt()));

        int id = 0;

        RS.INSTANCE.network.registerMessage(MessageTileDataParameter.class, MessageTileDataParameter.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageTileDataParameterUpdate.class, MessageTileDataParameterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemInsertHeld.class, MessageGridItemInsertHeld.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemPull.class, MessageGridItemPull.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingTransfer.class, MessageGridCraftingTransfer.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageWirelessGridSettingsUpdate.class, MessageWirelessGridSettingsUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingStart.class, MessageGridCraftingStart.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridPatternCreate.class, MessageGridPatternCreate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageCraftingMonitorCancel.class, MessageCraftingMonitorCancel.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemUpdate.class, MessageGridItemUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridItemDelta.class, MessageGridItemDelta.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidUpdate.class, MessageGridFluidUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidDelta.class, MessageGridFluidDelta.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidPull.class, MessageGridFluidPull.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridFluidInsertHeld.class, MessageGridFluidInsertHeld.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageProcessingPatternEncoderClear.class, MessageProcessingPatternEncoderClear.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridFilterUpdate.class, MessageGridFilterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingPreview.class, MessageGridCraftingPreview.class, id++, Side.SERVER);
        //RefinedStorage.INSTANCE.network.registerMessage(MessageGridCraftingPreviewResponse.class, MessageGridCraftingPreviewResponse.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageProcessingPatternEncoderTransfer.class, MessageProcessingPatternEncoderTransfer.class, id++, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(RS.INSTANCE, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new ContainerListener());

        registerTile(TileController.class, "controller");
        registerTile(TileGrid.class, "grid");
        registerTile(TileDiskDrive.class, "disk_drive");
        registerTile(TileExternalStorage.class, "external_storage");
        registerTile(TileImporter.class, "importer");
        registerTile(TileExporter.class, "exporter");
        registerTile(TileDetector.class, "detector");
        registerTile(TileSolderer.class, "solderer");
        registerTile(TileDestructor.class, "destructor");
        registerTile(TileConstructor.class, "constructor");
        registerTile(TileStorage.class, "storage");
        registerTile(TileRelay.class, "relay");
        registerTile(TileInterface.class, "interface");
        registerTile(TileCraftingMonitor.class, "crafting_monitor");
        registerTile(TileWirelessTransmitter.class, "wireless_transmitter");
        registerTile(TileCrafter.class, "crafter");
        registerTile(TileProcessingPatternEncoder.class, "processing_pattern_encoder");
        registerTile(TileCable.class, "cable");
        registerTile(TileNetworkReceiver.class, "network_receiver");
        registerTile(TileNetworkTransmitter.class, "network_transmitter");
        registerTile(TileFluidInterface.class, "fluid_interface");
        registerTile(TileFluidStorage.class, "fluid_storage");
        registerTile(TileDiskManipulator.class, "disk_manipulator");

        registerBlock(RSBlocks.CONTROLLER);
        registerBlock(RSBlocks.GRID);
        registerBlock(RSBlocks.CRAFTING_MONITOR);
        registerBlock(RSBlocks.CRAFTER);
        registerBlock(RSBlocks.PROCESSING_PATTERN_ENCODER);
        registerBlock(RSBlocks.DISK_DRIVE);
        registerBlock(RSBlocks.STORAGE);
        registerBlock(RSBlocks.FLUID_STORAGE);
        registerBlock(RSBlocks.SOLDERER);
        registerBlock(RSBlocks.CABLE);
        registerBlock(RSBlocks.IMPORTER);
        registerBlock(RSBlocks.EXPORTER);
        registerBlock(RSBlocks.EXTERNAL_STORAGE);
        registerBlock(RSBlocks.CONSTRUCTOR);
        registerBlock(RSBlocks.DESTRUCTOR);
        registerBlock(RSBlocks.DETECTOR);
        registerBlock(RSBlocks.RELAY);
        registerBlock(RSBlocks.INTERFACE);
        registerBlock(RSBlocks.FLUID_INTERFACE);
        registerBlock(RSBlocks.WIRELESS_TRANSMITTER);
        registerBlock(RSBlocks.MACHINE_CASING);
        registerBlock(RSBlocks.NETWORK_TRANSMITTER);
        registerBlock(RSBlocks.NETWORK_RECEIVER);
        registerBlock(RSBlocks.DISK_MANIPULATOR);

        registerItem(RSItems.QUARTZ_ENRICHED_IRON);
        registerItem(RSItems.STORAGE_DISK);
        registerItem(RSItems.FLUID_STORAGE_DISK);
        registerItem(RSItems.STORAGE_HOUSING);
        registerItem(RSItems.PATTERN);
        registerItem(RSItems.STORAGE_PART);
        registerItem(RSItems.FLUID_STORAGE_PART);
        registerItem(RSItems.WIRELESS_GRID);
        registerItem(RSItems.PROCESSOR);
        registerItem(RSItems.CORE);
        registerItem(RSItems.SILICON);
        registerItem(RSItems.UPGRADE);
        registerItem(RSItems.GRID_FILTER);
        registerItem(RSItems.NETWORK_CARD);

        OreDictionary.registerOre("itemSilicon", RSItems.SILICON);

        // Processors
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON));

        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

        // Silicon
        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(RSItems.SILICON), 0.5f);

        // Quartz Enriched Iron
        GameRegistry.addRecipe(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON, 4),
            "II",
            "IQ",
            'I', new ItemStack(Items.IRON_INGOT),
            'Q', new ItemStack(Items.QUARTZ)
        );

        // Machine Casing
        GameRegistry.addRecipe(new ItemStack(RSBlocks.MACHINE_CASING),
            "EEE",
            "E E",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        );

        // Construction Core
        GameRegistry.addShapelessRecipe(new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.GLOWSTONE_DUST)
        );

        // Destruction Core
        GameRegistry.addShapelessRecipe(new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.QUARTZ)
        );

        // Relay
        GameRegistry.addShapelessRecipe(new ItemStack(RSBlocks.RELAY),
            new ItemStack(RSBlocks.MACHINE_CASING),
            new ItemStack(RSBlocks.CABLE),
            new ItemStack(Blocks.REDSTONE_TORCH)
        );

        // Controller
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.CONTROLLER, 1, EnumControllerType.NORMAL.getId()),
            "EDE",
            "SMS",
            "ESE",
            'D', new ItemStack(Items.DIAMOND),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'S', "itemSilicon"
        ));

        // Solderer
        GameRegistry.addRecipe(new ItemStack(RSBlocks.SOLDERER),
            "ESE",
            "E E",
            "ESE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'S', new ItemStack(Blocks.STICKY_PISTON)
        );

        // Disk Drive
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.DISK_DRIVE),
            500,
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RSBlocks.MACHINE_CASING),
            new ItemStack(Blocks.CHEST)
        ));

        // Cable
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.CABLE, 12),
            "EEE",
            "GRG",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE)
        ));

        // Wireless Transmitter
        GameRegistry.addRecipe(new ItemStack(RSBlocks.WIRELESS_TRANSMITTER),
            "EPE",
            "EME",
            "EAE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'P', new ItemStack(Items.ENDER_PEARL),
            'M', new ItemStack(RSBlocks.MACHINE_CASING)
        );

        // Grid
        GameRegistry.addRecipe(new ItemStack(RSBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            "ECE",
            "PMP",
            "EDE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING)
        );

        // Crafting Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.CRAFTING.getId()),
            500,
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(Blocks.CRAFTING_TABLE)
        ));

        // Pattern Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.PATTERN.getId()),
            500,
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(RSItems.PATTERN)
        ));

        // Fluid Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.FLUID.getId()),
            500,
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(RSBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(Items.BUCKET)
        ));

        // Wireless Grid
        GameRegistry.addRecipe(new ItemStack(RSItems.WIRELESS_GRID, 1, ItemWirelessGrid.TYPE_NORMAL),
            "EPE",
            "EAE",
            "EEE",
            'P', new ItemStack(Items.ENDER_PEARL),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        );

        // Crafter
        GameRegistry.addRecipe(new ItemStack(RSBlocks.CRAFTER),
            "ECE",
            "AMA",
            "EDE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING)
        );

        // Processing Pattern Encoder
        GameRegistry.addRecipe(new ItemStack(RSBlocks.PROCESSING_PATTERN_ENCODER),
            "ECE",
            "PMP",
            "EFE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'P', new ItemStack(RSItems.PATTERN),
            'C', new ItemStack(Blocks.CRAFTING_TABLE),
            'F', new ItemStack(Blocks.FURNACE)
        );

        // External Storage
        GameRegistry.addRecipe(new ItemStack(RSBlocks.EXTERNAL_STORAGE),
            "CED",
            "HMH",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'H', new ItemStack(Blocks.CHEST),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.CABLE),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Importer
        GameRegistry.addShapelessRecipe(new ItemStack(RSBlocks.IMPORTER),
            new ItemStack(RSBlocks.CABLE),
            new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Exporter
        GameRegistry.addShapelessRecipe(new ItemStack(RSBlocks.EXPORTER),
            new ItemStack(RSBlocks.CABLE),
            new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Destructor
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.DESTRUCTOR),
            "EDE",
            "RMR",
            "EIE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(RSBlocks.CABLE),
            'I', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Constructor
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.CONSTRUCTOR),
            "ECE",
            "RMR",
            "EIE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(RSBlocks.CABLE),
            'I', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Detector
        GameRegistry.addRecipe(new ItemStack(RSBlocks.DETECTOR),
            "ECE",
            "RMR",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'R', new ItemStack(Items.REDSTONE),
            'C', new ItemStack(Items.COMPARATOR),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Storage Parts
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
            "SES",
            "GRG",
            "SGS",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'S', "itemSilicon",
            'G', "blockGlass"
        ));

        GameRegistry.addRecipe(new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'S', new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K)
        );

        GameRegistry.addRecipe(new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'S', new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K)
        );

        GameRegistry.addRecipe(new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'S', new ItemStack(RSItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K)
        );

        // Fluid Storage Parts
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_64K),
            "SES",
            "GRG",
            "SGS",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'S', "itemSilicon",
            'G', "blockGlass"
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_128K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'S', new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_64K)
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_256K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'S', new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_128K)
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_512K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'S', new ItemStack(RSItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_256K)
        ));

        // Storage Housing
        GameRegistry.addRecipe(new ShapedOreRecipe(ItemStorageNBT.createStackWithNBT(new ItemStack(RSItems.STORAGE_HOUSING)),
            "GRG",
            "R R",
            "EEE",
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        ));

        // Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = ItemStorageNBT.createStackWithNBT(new ItemStack(RSItems.STORAGE_DISK, 1, type));

            GameRegistry.addRecipe(new ShapedOreRecipe(disk,
                "GRG",
                "RPR",
                "EEE",
                'G', "blockGlass",
                'R', new ItemStack(Items.REDSTONE),
                'P', new ItemStack(RSItems.STORAGE_PART, 1, type),
                'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
            ));

            GameRegistry.addShapelessRecipe(disk,
                new ItemStack(RSItems.STORAGE_HOUSING),
                new ItemStack(RSItems.STORAGE_PART, 1, type)
            );
        }

        // Fluid Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = FluidStorageNBT.createStackWithNBT(new ItemStack(RSItems.FLUID_STORAGE_DISK, 1, type));

            GameRegistry.addRecipe(new ShapedOreRecipe(disk,
                "GRG",
                "RPR",
                "EEE",
                'G', "blockGlass",
                'R', new ItemStack(Items.REDSTONE),
                'P', new ItemStack(RSItems.FLUID_STORAGE_PART, 1, type),
                'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
            ));

            GameRegistry.addShapelessRecipe(disk,
                new ItemStack(RSItems.STORAGE_HOUSING),
                new ItemStack(RSItems.FLUID_STORAGE_PART, 1, type)
            );
        }

        // Pattern
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.PATTERN),
            "GRG",
            "RGR",
            "EEE",
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        ));

        // Upgrade
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.UPGRADE, 1, 0),
            "EGE",
            "EPE",
            "EGE",
            'G', "blockGlass",
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        ));

        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_RANGE));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SPEED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_INTERDIMENSIONAL));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_CRAFTING));

        GameRegistry.addShapedRecipe(new ItemStack(RSItems.UPGRADE, 1, ItemUpgrade.TYPE_STACK),
            "USU",
            "SUS",
            "USU",
            'U', new ItemStack(Items.SUGAR),
            'S', new ItemStack(RSItems.UPGRADE, 1, ItemUpgrade.TYPE_SPEED)
        );

        // Storage Blocks
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Fluid Storage Blocks
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_64K, ItemFluidStoragePart.TYPE_64K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_128K, ItemFluidStoragePart.TYPE_128K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_256K, ItemFluidStoragePart.TYPE_256K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_512K, ItemFluidStoragePart.TYPE_512K));

        // Crafting Monitor
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.CRAFTING_MONITOR),
            "EGE",
            "GMG",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'G', "blockGlass",
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        ));

        // Interface
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.INTERFACE),
            200,
            new ItemStack(RSBlocks.IMPORTER),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RSBlocks.EXPORTER)
        ));

        // Fluid Interface
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.FLUID_INTERFACE),
            200,
            new ItemStack(Items.BUCKET),
            new ItemStack(RSBlocks.INTERFACE),
            new ItemStack(Items.BUCKET)
        ));

        // Grid Filter
        GameRegistry.addShapedRecipe(new ItemStack(RSItems.GRID_FILTER),
            "EPE",
            "PHP",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.PAPER),
            'H', new ItemStack(Blocks.HOPPER)
        );

        // Network Card
        GameRegistry.addShapedRecipe(new ItemStack(RSItems.NETWORK_CARD),
            "EEE",
            "PAP",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.PAPER),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Network Transmitter
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.NETWORK_TRANSMITTER),
            "EEE",
            "CMD",
            "AAA",
            'E', new ItemStack(Items.ENDER_PEARL),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Network Receiver
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.NETWORK_RECEIVER),
            "AAA",
            "CMD",
            "EEE",
            'E', new ItemStack(Items.ENDER_PEARL),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Disk Manipulator
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.DISK_MANIPULATOR),
            "ESE",
            "CMD",
            "ESE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'S', new ItemStack(RSItems.STORAGE_HOUSING),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION)
        );
    }

    public void init(FMLInitializationEvent e) {
        // NO OP
    }

    public void postInit(FMLPostInitializationEvent e) {
        // NO OP
    }

    private void registerBlock(BlockBase block) {
        GameRegistry.<Block>register(block);
        GameRegistry.register(block.createItem());
    }

    private void registerBlock(BlockCable cable) {
        GameRegistry.<Block>register(cable);
        GameRegistry.register(new ItemBlockBase(cable, cable.getPlacementType(), false));

        cableTypes.add(cable);
    }

    private void registerTile(Class<? extends TileBase> tile, String id) {
        GameRegistry.registerTileEntity(tile, RS.ID + ":" + id);

        try {
            TileBase tileInstance = tile.newInstance();

            tileInstance.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }
}
