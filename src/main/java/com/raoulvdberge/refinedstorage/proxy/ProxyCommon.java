package com.raoulvdberge.refinedstorage.proxy;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.*;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerFluids;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRedstone;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRecipePrintedProcessor;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRecipeProcessor;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRecipeUpgrade;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.gui.GuiHandler;
import com.raoulvdberge.refinedstorage.integration.craftingtweaks.IntegrationCraftingTweaks;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.ReaderWriterHandlerForgeEnergy;
import com.raoulvdberge.refinedstorage.integration.oc.IntegrationOC;
import com.raoulvdberge.refinedstorage.integration.tesla.IntegrationTesla;
import com.raoulvdberge.refinedstorage.item.ItemBlockBase;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.data.ContainerListener;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ProxyCommon {
    protected List<BlockCable> cableTypes = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent e) {
        CapabilityNetworkNodeProxy.register();

        API.deliver(e.getAsmData());

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(buf.readInt(), ByteBufUtils.readItemStack(buf), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(buf.readInt(), RSUtils.readFluidStack(buf).getRight(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementText.ID, buf -> new CraftingMonitorElementText(ByteBufUtils.readUTF8String(buf), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementError.ID, buf -> {
            String id = ByteBufUtils.readUTF8String(buf);
            String tooltip = ByteBufUtils.readUTF8String(buf);

            return new CraftingMonitorElementError(API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf), tooltip);
        });
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementInfo.ID, buf -> {
            String id = ByteBufUtils.readUTF8String(buf);
            String tooltip = ByteBufUtils.readUTF8String(buf);

            return new CraftingMonitorElementInfo(API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf), tooltip);
        });

        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementItemStack.ID, CraftingPreviewElementItemStack::fromByteBuf);
        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementFluidStack.ID, CraftingPreviewElementFluidStack::fromByteBuf);

        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerItems.ID, ReaderWriterHandlerItems::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerFluids.ID, ReaderWriterHandlerFluids::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerRedstone.ID, tag -> new ReaderWriterHandlerRedstone());
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerForgeEnergy.ID, ReaderWriterHandlerForgeEnergy::new);

        if (IntegrationCraftingTweaks.isLoaded()) {
            IntegrationCraftingTweaks.register();
        }

        if (IntegrationTesla.isLoaded()) {
            IntegrationTesla.register();
        }

        int id = 0;

        RS.INSTANCE.network.registerMessage(MessageTileDataParameter.class, MessageTileDataParameter.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageTileDataParameterUpdate.class, MessageTileDataParameterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemInsertHeld.class, MessageGridItemInsertHeld.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemPull.class, MessageGridItemPull.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingTransfer.class, MessageGridCraftingTransfer.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridSettingsUpdate.class, MessageGridSettingsUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingStart.class, MessageGridCraftingStart.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridPatternCreate.class, MessageGridPatternCreate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageCraftingMonitorCancel.class, MessageCraftingMonitorCancel.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageCraftingMonitorElements.class, MessageCraftingMonitorElements.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridItemUpdate.class, MessageGridItemUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridItemDelta.class, MessageGridItemDelta.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidUpdate.class, MessageGridFluidUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidDelta.class, MessageGridFluidDelta.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridFluidPull.class, MessageGridFluidPull.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridFluidInsertHeld.class, MessageGridFluidInsertHeld.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageProcessingPatternEncoderClear.class, MessageProcessingPatternEncoderClear.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageFilterUpdate.class, MessageFilterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingPreview.class, MessageGridCraftingPreview.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingPreviewResponse.class, MessageGridCraftingPreviewResponse.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingStartResponse.class, MessageGridCraftingStartResponse.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageProcessingPatternEncoderTransfer.class, MessageProcessingPatternEncoderTransfer.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterUpdate.class, MessageReaderWriterUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterChannelAdd.class, MessageReaderWriterChannelAdd.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterChannelRemove.class, MessageReaderWriterChannelRemove.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageSecurityManagerUpdate.class, MessageSecurityManagerUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageWirelessFluidGridSettingsUpdate.class, MessageWirelessFluidGridSettingsUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageWirelessCraftingMonitorViewAutomated.class, MessageWirelessCraftingMonitorViewAutomated.class, id++, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(RS.INSTANCE, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new ContainerListener());
        MinecraftForge.EVENT_BUS.register(new NetworkNodeListener());

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
        registerTile(TileSecurityManager.class, "security_manager");
        registerTile(TileReader.class, "reader");
        registerTile(TileWriter.class, "writer");
        registerTile(TileStorageMonitor.class, "storage_monitor");
        registerTile(TilePortableGrid.class, "portable_grid");

        registerBlock(RSBlocks.CONTROLLER);
        registerBlock(RSBlocks.GRID);
        registerBlock(RSBlocks.PORTABLE_GRID);
        registerBlock(RSBlocks.CRAFTING_MONITOR);
        registerBlock(RSBlocks.STORAGE_MONITOR);
        registerBlock(RSBlocks.SECURITY_MANAGER);
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
        registerBlock(RSBlocks.READER);
        registerBlock(RSBlocks.WRITER);
        registerBlock(RSBlocks.DETECTOR);
        registerBlock(RSBlocks.RELAY);
        registerBlock(RSBlocks.INTERFACE);
        registerBlock(RSBlocks.FLUID_INTERFACE);
        registerBlock(RSBlocks.WIRELESS_TRANSMITTER);
        registerBlock(RSBlocks.MACHINE_CASING);
        registerBlock(RSBlocks.QUARTZ_ENRICHED_IRON);
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
        registerItem(RSItems.WIRELESS_FLUID_GRID);
        registerItem(RSItems.WIRELESS_CRAFTING_MONITOR);
        registerItem(RSItems.PROCESSOR);
        registerItem(RSItems.CORE);
        registerItem(RSItems.SILICON);
        registerItem(RSItems.UPGRADE);
        registerItem(RSItems.FILTER);
        registerItem(RSItems.NETWORK_CARD);
        registerItem(RSItems.WRENCH);
        registerItem(RSItems.SECURITY_CARD);

        OreDictionary.registerOre("itemSilicon", RSItems.SILICON);

        // Processors
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC, false));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED, false));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED, false));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC, true));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED, true));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED, true));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON, false));

        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

        // Silicon
        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(RSItems.SILICON), 0.5f);

        /*
        // Quartz Enriched Iron
        GameRegistry.addRecipe(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON, 4),
            "II",
            "IQ",
            'I', new ItemStack(Items.IRON_INGOT),
            'Q', new ItemStack(Items.QUARTZ)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON, 9), new ItemStack(RSBlocks.QUARTZ_ENRICHED_IRON));

        // Block of Quartz Enriched Iron
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.QUARTZ_ENRICHED_IRON),
            "EEE",
            "EEE",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        );

        // Machine Casing
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.MACHINE_CASING),
            "EEE",
            "ESE",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'S', "stone"
        ));

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.CONTROLLER, 1, ControllerType.NORMAL.getId()),
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
        GameRegistry.addRecipe(new ItemStack(RSBlocks.DISK_DRIVE),
            "ECE",
            "EME",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'C', new ItemStack(Blocks.CHEST),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

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
        GameRegistry.addRecipe(new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()),
            "ECE",
            "PMP",
            "EDE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING)
        );*/

        // Crafting Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, GridType.CRAFTING.getId()),
            500,
            OreDictionary.getOres("workbench"),
            NonNullList.withSize(1, new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)),
            NonNullList.withSize(1, new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()))
        ));

        // Pattern Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, GridType.PATTERN.getId()),
            500,
            NonNullList.withSize(1, new ItemStack(RSItems.PATTERN)),
            NonNullList.withSize(1, new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)),
            NonNullList.withSize(1, new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()))
        ));

        // Fluid Grid
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.GRID, 1, GridType.FLUID.getId()),
            500,
            NonNullList.withSize(1, new ItemStack(Items.BUCKET)),
            NonNullList.withSize(1, new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)),
            NonNullList.withSize(1, new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()))
        ));

        /*
        // Wireless Grid
        GameRegistry.addRecipe(new ItemStack(RSItems.WIRELESS_GRID, 1, ItemWirelessGrid.TYPE_NORMAL),
            "EPE",
            "EGE",
            "EAE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.ENDER_PEARL),
            'G', new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Portable Grid
        GameRegistry.addRecipe(new ItemStack(RSBlocks.PORTABLE_GRID),
            "EGE",
            "ECE",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'G', new ItemStack(RSBlocks.GRID, 1, GridType.NORMAL.getId()),
            'C', new ItemStack(RSBlocks.CONTROLLER, 1, ControllerType.NORMAL.getId())
        );

        // Wireless Fluid Grid
        GameRegistry.addRecipe(new ItemStack(RSItems.WIRELESS_FLUID_GRID, 1, ItemWirelessFluidGrid.TYPE_NORMAL),
            "EPE",
            "EGE",
            "EAE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.ENDER_PEARL),
            'G', new ItemStack(RSBlocks.GRID, 1, GridType.FLUID.getId()),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Wireless Crafting Monitor
        GameRegistry.addRecipe(new ItemStack(RSItems.WIRELESS_CRAFTING_MONITOR, 1, ItemWirelessCraftingMonitor.TYPE_NORMAL),
            "EPE",
            "EME",
            "EAE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.ENDER_PEARL),
            'M', new ItemStack(RSBlocks.CRAFTING_MONITOR),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Writer
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.WRITER),
            "ECE",
            "RMR",
            "EIE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(RSBlocks.CABLE),
            'I', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Reader
        GameRegistry.addShapedRecipe(new ItemStack(RSBlocks.READER),
            "EDE",
            "RMR",
            "EIE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(RSBlocks.CABLE),
            'I', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.PROCESSING_PATTERN_ENCODER),
            "ECE",
            "PMP",
            "EFE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'P', new ItemStack(RSItems.PATTERN),
            'C', "workbench",
            'F', new ItemStack(Blocks.FURNACE)
        ));

        // External Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.EXTERNAL_STORAGE),
            "CED",
            "HMH",
            "EPE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'H', "chest",
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.CABLE),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        ));

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSItems.STORAGE_HOUSING),
            "GRG",
            "R R",
            "EEE",
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)
        ));

        // Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = StorageDiskItem.initDisk(new ItemStack(RSItems.STORAGE_DISK, 1, type));

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
            ItemStack disk = StorageDiskFluid.initDisk(new ItemStack(RSItems.FLUID_STORAGE_DISK, 1, type));

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
        ));*/

        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_RANGE));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SPEED));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_INTERDIMENSIONAL));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SILK_TOUCH));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_CRAFTING));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.initializeForFortune(1)));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.initializeForFortune(2)));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.initializeForFortune(3)));

        /*GameRegistry.addShapedRecipe(new ItemStack(RSItems.UPGRADE, 1, ItemUpgrade.TYPE_STACK),
            "USU",
            "SUS",
            "USU",
            'U', new ItemStack(Items.SUGAR),
            'S', new ItemStack(RSItems.UPGRADE, 1, ItemUpgrade.TYPE_SPEED)
        );

        // Storage Blocks
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(ItemStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(ItemStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(ItemStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(ItemStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Fluid Storage Blocks
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(FluidStorageType.TYPE_64K, ItemFluidStoragePart.TYPE_64K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(FluidStorageType.TYPE_128K, ItemFluidStoragePart.TYPE_128K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(FluidStorageType.TYPE_256K, ItemFluidStoragePart.TYPE_256K));
        API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(FluidStorageType.TYPE_512K, ItemFluidStoragePart.TYPE_512K));

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
            NonNullList.withSize(1, new ItemStack(RSBlocks.IMPORTER)),
            NonNullList.withSize(1, new ItemStack(RSBlocks.EXPORTER)),
            NonNullList.withSize(1, new ItemStack(RSBlocks.MACHINE_CASING))
        ));

        // Fluid Interface
        API.instance().getSoldererRegistry().addRecipe(API.instance().getSoldererRegistry().createSimpleRecipe(
            new ItemStack(RSBlocks.FLUID_INTERFACE),
            200,
            NonNullList.withSize(1, new ItemStack(Items.BUCKET)),
            OreDictionary.getOres("dustRedstone"),
            NonNullList.withSize(1, new ItemStack(RSBlocks.INTERFACE))
        ));

        // Grid Filter
        GameRegistry.addShapedRecipe(new ItemStack(RSItems.FILTER),
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

        // Wrench
        GameRegistry.addShapedRecipe(new ItemStack(RSItems.WRENCH),
            "EPE",
            "EEE",
            " E ",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC)
        );

        // Security Manager
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.SECURITY_MANAGER),
            "ECE",
            "SMS",
            "ESE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'S', new ItemStack(RSItems.SECURITY_CARD),
            'C', "chest"
        ));

        // Security Card
        GameRegistry.addShapedRecipe(new ItemStack(RSItems.SECURITY_CARD),
            "EEE",
            "CAC",
            "EEE",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'C', new ItemStack(RSItems.NETWORK_CARD),
            'A', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Storage Monitor
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RSBlocks.STORAGE_MONITOR),
            "PCG",
            "EMG",
            "PDG",
            'E', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'C', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(RSItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(RSBlocks.MACHINE_CASING),
            'G', "blockGlass"
        ));*/
    }

    public void init(FMLInitializationEvent e) {
        if (IntegrationOC.isLoaded()) {
            //DriverNetwork.register();
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
        // NO OP
    }

    public void fixMappings(FMLMissingMappingsEvent e) {
        for (FMLMissingMappingsEvent.MissingMapping missing : e.getAll()) {
            if (missing.resourceLocation.getResourceDomain().equals(RS.ID) && missing.resourceLocation.getResourcePath().equals("grid_filter")) {
                missing.remap(RSItems.FILTER);
            }
        }
    }

    private void registerBlock(BlockBase block) {
        GameRegistry.<Block>register(block);
        GameRegistry.register(block.createItem());
    }

    private void registerBlock(BlockCable cable) {
        GameRegistry.<Block>register(cable);
        GameRegistry.register(new ItemBlockBase(cable, cable.getDirection(), false));

        cableTypes.add(cable);
    }

    private void registerTile(Class<? extends TileBase> tile, String id) {
        GameRegistry.registerTileEntity(tile, RS.ID + ":" + id);

        try {
            TileBase tileInstance = tile.newInstance();

            if (tileInstance instanceof TileNode) {
                String nodeId = ((TileNode) tileInstance).createNode(null, null).getId();

                API.instance().getNetworkNodeRegistry().add(nodeId, (tag, world, pos) -> {
                    NetworkNode node = ((TileNode) tileInstance).createNode(world, pos);

                    node.read(tag);

                    return node;
                });
            }

            tileInstance.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }
}
