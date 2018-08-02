package com.raoulvdberge.refinedstorage.proxy;

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
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryItem;
import com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage.ExternalStorageProviderFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage.ExternalStorageProviderItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
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
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.recipe.RecipeCover;
import com.raoulvdberge.refinedstorage.recipe.RecipeHollowCover;
import com.raoulvdberge.refinedstorage.recipe.RecipeUpgradeWithEnchantedBook;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessFluidGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import java.util.LinkedList;
import java.util.List;

public class ProxyCommon {
    protected List<Item> itemsToRegister = new LinkedList<>();
    protected List<BlockBase> blocksToRegister = new LinkedList<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        CapabilityNetworkNodeProxy.register();

        API.deliver(e.getAsmData());

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(StackUtils.readItemStack(buf), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(StackUtils.readFluidStack(buf), buf.readInt(), buf.readInt()));
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
        RS.INSTANCE.network.registerMessage(MessageConfigSync.class, MessageConfigSync.class, id++, Side.CLIENT);
        RS.INSTANCE.network.registerMessage(MessageSlotFilterFluidSetAmount.class, MessageSlotFilterFluidSetAmount.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageSlotFilterSet.class, MessageSlotFilterSet.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageSlotFilterSetFluid.class, MessageSlotFilterSetFluid.class, id++, Side.SERVER);
        RS.INSTANCE.network.registerMessage(MessageSlotFilterFluidUpdate.class, MessageSlotFilterFluidUpdate.class, id++, Side.CLIENT);

        NetworkRegistry.INSTANCE.registerGuiHandler(RS.INSTANCE, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new NetworkNodeListener());

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
        registerItem(RSItems.CUTTING_TOOL);
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
        registerItem(RSItems.COVER);
        registerItem(RSItems.HOLLOW_COVER);
        registerItem(RSItems.WRENCH);

        IntegrationInventorySorter.register();
    }

    public void init(FMLInitializationEvent e) {
        OreDictionary.registerOre("itemSilicon", RSItems.SILICON);

        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(RSItems.SILICON), 0.5F);

        GameRegistry.addSmelting(new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_CUT_BASIC), new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC), 0.5F);
        GameRegistry.addSmelting(new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_CUT_IMPROVED), new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED), 0.5F);
        GameRegistry.addSmelting(new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_CUT_ADVANCED), new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED), 0.5F);

        WirelessGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryWirelessGrid());
        WirelessFluidGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryWirelessFluidGrid());
        PortableGrid.ID = API.instance().getWirelessGridRegistry().add(new WirelessGridFactoryPortableGrid());

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
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        e.getRegistry().register(new RecipeCover().setRegistryName(new ResourceLocation(RS.ID, "cover")));
        e.getRegistry().register(new RecipeHollowCover().setRegistryName(new ResourceLocation(RS.ID, "hollow_cover")));
        e.getRegistry().register(new RecipeUpgradeWithEnchantedBook("fortune", 1, ItemUpgrade.TYPE_FORTUNE_1).setRegistryName(new ResourceLocation(RS.ID, "fortune_1_upgrade")));
        e.getRegistry().register(new RecipeUpgradeWithEnchantedBook("fortune", 2, ItemUpgrade.TYPE_FORTUNE_2).setRegistryName(new ResourceLocation(RS.ID, "fortune_2_upgrade")));
        e.getRegistry().register(new RecipeUpgradeWithEnchantedBook("fortune", 3, ItemUpgrade.TYPE_FORTUNE_3).setRegistryName(new ResourceLocation(RS.ID, "fortune_3_upgrade")));
        e.getRegistry().register(new RecipeUpgradeWithEnchantedBook("silk_touch", 1, ItemUpgrade.TYPE_SILK_TOUCH).setRegistryName(new ResourceLocation(RS.ID, "silk_touch_upgrade")));
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BlockBase) {
            e.setCanHarvest(true); // Allow break without tool
        }
    }

    @SubscribeEvent
    public void onPlayerLoginEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e) {
        if (!e.player.world.isRemote) {
            RS.INSTANCE.network.sendTo(new MessageConfigSync(), (EntityPlayerMP) e.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogoutEvent(WorldEvent.Unload e) {
        if (e.getWorld().isRemote && RS.INSTANCE.config.getOriginalClientVersion() != null) {
            RS.INSTANCE.config = RS.INSTANCE.config.getOriginalClientVersion();
        }
    }

    @SubscribeEvent
    public void fixItemMappings(RegistryEvent.MissingMappings<Item> e) {
        OneSixMigrationHelper.removalHook();

        for (RegistryEvent.MissingMappings.Mapping<Item> missing : e.getMappings()) {
            if (missing.key.getNamespace().equals(RS.ID) && (missing.key.getPath().equals("wrench") || missing.key.getPath().equals("solderer"))) {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void fixBlockMappings(RegistryEvent.MissingMappings<Block> e) {
        OneSixMigrationHelper.removalHook();

        for (RegistryEvent.MissingMappings.Mapping<Block> missing : e.getMappings()) {
            if (missing.key.getNamespace().equals(RS.ID) && missing.key.getPath().equals("solderer")) {
                missing.ignore();
            }
        }
    }

    private void registerBlock(BlockBase block) {
        blocksToRegister.add(block);

        registerItem(block.createItem());

        if (block.getInfo().hasTileEntity()) {
            registerTile(block.getInfo());
        }
    }

    private void registerItem(Item item) {
        itemsToRegister.add(item);
    }

    private void registerTile(IBlockInfo info) {
        Class<? extends TileBase> clazz = info.createTileEntity().getClass();

        GameRegistry.registerTileEntity(clazz, info.getId());

        try {
            TileBase tileInstance = clazz.newInstance();

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
