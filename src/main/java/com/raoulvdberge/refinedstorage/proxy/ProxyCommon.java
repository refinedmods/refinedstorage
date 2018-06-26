package com.raoulvdberge.refinedstorage.proxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementColor;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless.WirelessGridFactoryPortableGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless.WirelessGridFactoryWirelessFluidGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless.WirelessGridFactoryWirelessGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerFluids;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRedstone;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRecipeLoader;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryItem;
import com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage.ExternalStorageProviderFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage.ExternalStorageProviderItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.container.ContainerCrafter;
import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import com.raoulvdberge.refinedstorage.container.slot.SlotCrafterManager;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiHandler;
import com.raoulvdberge.refinedstorage.integration.craftingtweaks.IntegrationCraftingTweaks;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.ReaderWriterHandlerForgeEnergy;
import com.raoulvdberge.refinedstorage.integration.funkylocomotion.IntegrationFunkyLocomotion;
import com.raoulvdberge.refinedstorage.integration.funkylocomotion.MoveFactoryRegisterer;
import com.raoulvdberge.refinedstorage.integration.inventorysorter.IntegrationInventorySorter;
import com.raoulvdberge.refinedstorage.integration.oc.DriverNetwork;
import com.raoulvdberge.refinedstorage.integration.oc.IntegrationOC;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessFluidGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ProxyCommon {
    private List<Item> itemsToRegister = new LinkedList<>();
    private List<BlockBase> blocksToRegister = new LinkedList<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        CapabilityNetworkNodeProxy.register();

        API.deliver(e.getAsmData());

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(ByteBufUtils.readItemStack(buf), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(StackUtils.readFluidStack(buf).getRight(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementText.ID, buf -> new CraftingMonitorElementText(ByteBufUtils.readUTF8String(buf), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementColor.ID, buf -> {
            int color = buf.readInt();
            String id = ByteBufUtils.readUTF8String(buf);
            String tooltip = ByteBufUtils.readUTF8String(buf);

            return new CraftingMonitorElementColor(API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf), tooltip, color);
        });

        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementItemStack.ID, CraftingPreviewElementItemStack::fromByteBuf);
        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementFluidStack.ID, CraftingPreviewElementFluidStack::fromByteBuf);
        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementError.ID, CraftingPreviewElementError::fromByteBuf);

        API.instance().addPatternRenderHandler(pattern -> GuiBase.isShiftKeyDown());
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerCrafterManager) {
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof SlotCrafterManager && slot.getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerCrafter) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });

        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerItems.ID, ReaderWriterHandlerItems::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerFluids.ID, ReaderWriterHandlerFluids::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerRedstone.ID, tag -> new ReaderWriterHandlerRedstone());
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerForgeEnergy.ID, ReaderWriterHandlerForgeEnergy::new);

        API.instance().getStorageDiskRegistry().add(StorageDiskFactoryItem.ID, new StorageDiskFactoryItem());
        API.instance().getStorageDiskRegistry().add(StorageDiskFactoryFluid.ID, new StorageDiskFactoryFluid());

        API.instance().addExternalStorageProvider(StorageType.ITEM, new ExternalStorageProviderItem());
        API.instance().addExternalStorageProvider(StorageType.FLUID, new ExternalStorageProviderFluid());

        int id = 0;

        RS.INSTANCE.network.registerMessage(MessageTileDataParameter.class, MessageTileDataParameter.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageTileDataParameterUpdate.class, MessageTileDataParameterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemInsertHeld.class, MessageGridItemInsertHeld.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridItemPull.class, MessageGridItemPull.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridClear.class, MessageGridClear.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridTransfer.class, MessageGridTransfer.class, id++, Side.SERVER);
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
        RS.INSTANCE.network.registerMessage(MessageFilterUpdate.class, MessageFilterUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingPreview.class, MessageGridCraftingPreview.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingPreviewResponse.class, MessageGridCraftingPreviewResponse.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridCraftingStartResponse.class, MessageGridCraftingStartResponse.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageGridProcessingTransfer.class, MessageGridProcessingTransfer.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterUpdate.class, MessageReaderWriterUpdate.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterChannelAdd.class, MessageReaderWriterChannelAdd.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageReaderWriterChannelRemove.class, MessageReaderWriterChannelRemove.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageSecurityManagerUpdate.class, MessageSecurityManagerUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageWirelessFluidGridSettingsUpdate.class, MessageWirelessFluidGridSettingsUpdate.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageCrafterManagerSlotSizes.class, MessageCrafterManagerSlotSizes.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageCrafterManagerRequestSlotData.class, MessageCrafterManagerRequestSlotData.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageWirelessCraftingMonitorSettings.class, MessageWirelessCraftingMonitorSettings.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageStorageDiskSizeRequest.class, MessageStorageDiskSizeRequest.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageStorageDiskSizeResponse.class, MessageStorageDiskSizeResponse.class, id++, Side.CLIENT);

        NetworkRegistry.INSTANCE.registerGuiHandler(RS.INSTANCE, new GuiHandler());

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
        registerTile(TileCrafterManager.class, "crafter_manager");

        registerBlock(RSBlocks.CONTROLLER);
        registerBlock(RSBlocks.GRID);
        registerBlock(RSBlocks.PORTABLE_GRID);
        registerBlock(RSBlocks.CRAFTING_MONITOR);
        registerBlock(RSBlocks.STORAGE_MONITOR);
        registerBlock(RSBlocks.SECURITY_MANAGER);
        registerBlock(RSBlocks.CRAFTER);
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
        registerBlock(RSBlocks.CRAFTER_MANAGER);

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
        registerItem(RSItems.SECURITY_CARD);

        IntegrationInventorySorter.register();
    }

    public void init(FMLInitializationEvent e) {
        OreDictionary.registerOre("itemSilicon", RSItems.SILICON);

        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(RSItems.SILICON), 0.5f);

        WirelessGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryWirelessGrid());
        WirelessFluidGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryWirelessFluidGrid());
        PortableGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryPortableGrid());

        CraftingHelper.register(new ResourceLocation(RS.ID + ":enchanted_book"), new IIngredientFactory() {
            @Nonnull
            @Override
            public Ingredient parse(JsonContext context, JsonObject json) {
                String id = JsonUtils.getString(json, "id");
                int level = JsonUtils.getInt(json, "level", 1);

                Enchantment enchantment = Enchantment.getEnchantmentByLocation(id);

                if (enchantment == null) {
                    throw new JsonSyntaxException("Couldn't find enchantment with id '" + id + "'");
                }

                return Ingredient.fromStacks(ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, level)));
            }
        });

        SoldererRecipeLoader.load();

        if (IntegrationOC.isLoaded()) {
            DriverNetwork.register();
        }

        if (IntegrationCraftingTweaks.isLoaded()) {
            IntegrationCraftingTweaks.register();
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
        // NO OP
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        blocksToRegister.forEach(e.getRegistry()::register);

        if (IntegrationFunkyLocomotion.isLoaded()) {
            MoveFactoryRegisterer.register(blocksToRegister);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        itemsToRegister.forEach(e.getRegistry()::register);
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BlockBase) {
            e.setCanHarvest(true); // Allow break without tool
        }
    }

    @SubscribeEvent
    public void fixItemMappings(RegistryEvent.MissingMappings<Item> e) {
        OneSixMigrationHelper.removalHook();

        for (RegistryEvent.MissingMappings.Mapping<Item> missing : e.getMappings()) {
            if (missing.key.getResourceDomain().equals(RS.ID) && missing.key.getResourcePath().equals("wrench")) {
                missing.ignore();
            }
        }
    }

    private void registerBlock(BlockBase block) {
        blocksToRegister.add(block);

        registerItem(block.createItem());
    }

    private void registerItem(Item item) {
        itemsToRegister.add(item);
    }

    private void registerTile(Class<? extends TileBase> tile, String id) {
        GameRegistry.registerTileEntity(tile, new ResourceLocation(RS.ID, id));

        try {
            TileBase tileInstance = tile.newInstance();

            if (tileInstance instanceof TileNode) {
                API.instance().getNetworkNodeRegistry().add(((TileNode) tileInstance).getNodeId(), (tag, world, pos) -> {
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
}
