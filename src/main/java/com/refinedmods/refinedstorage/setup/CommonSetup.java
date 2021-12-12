package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ErrorCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.FluidCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ItemCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTaskFactory;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkListener;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeListener;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.*;
import com.refinedmods.refinedstorage.apiimpl.network.node.*;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.FluidExternalStorageProvider;
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.ItemExternalStorageProvider;
import com.refinedmods.refinedstorage.block.BlockListener;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.container.*;
import com.refinedmods.refinedstorage.container.factory.*;
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration;
import com.refinedmods.refinedstorage.integration.inventorysorter.InventorySorterIntegration;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import com.refinedmods.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer;
import com.refinedmods.refinedstorage.tile.*;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup {
    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent e) {
        RS.NETWORK_HANDLER.register();

        NetworkNodeProxyCapability.register();

        MinecraftForge.EVENT_BUS.register(new NetworkNodeListener());
        MinecraftForge.EVENT_BUS.register(new NetworkListener());
        MinecraftForge.EVENT_BUS.register(new BlockListener());

        API.instance().getStorageDiskRegistry().add(ItemStorageDiskFactory.ID, new ItemStorageDiskFactory());
        API.instance().getStorageDiskRegistry().add(FluidStorageDiskFactory.ID, new FluidStorageDiskFactory());

        API.instance().getNetworkNodeRegistry().add(DiskDriveNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new DiskDriveNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CableNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CableNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.NORMAL)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.CRAFTING_ID, (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.CRAFTING)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.PATTERN_ID, (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.PATTERN)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.FLUID_ID, (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.FLUID)));

        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.ONE_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.SIXTEEN_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.SIXTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.CREATIVE)));

        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new FluidStorageNetworkNode(world, pos, FluidStorageType.SIXTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new FluidStorageNetworkNode(world, pos, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K)));
        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new FluidStorageNetworkNode(world, pos, FluidStorageType.THOUSAND_TWENTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new FluidStorageNetworkNode(world, pos, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K)));
        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID, (tag, world, pos) -> readAndReturn(tag, new FluidStorageNetworkNode(world, pos, FluidStorageType.CREATIVE)));

        API.instance().getNetworkNodeRegistry().add(ExternalStorageNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ExternalStorageNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(ImporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ImporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(ExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(NetworkReceiverNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new NetworkReceiverNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(NetworkTransmitterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new NetworkTransmitterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(RelayNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new RelayNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(DetectorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new DetectorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(SecurityManagerNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new SecurityManagerNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(InterfaceNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new InterfaceNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(FluidInterfaceNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new FluidInterfaceNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(WirelessTransmitterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new WirelessTransmitterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(StorageMonitorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new StorageMonitorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(ConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ConstructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(DestructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new DestructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(DiskManipulatorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new DiskManipulatorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CrafterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CrafterManagerNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CrafterManagerNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CraftingMonitorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CraftingMonitorNetworkNode(world, pos)));

        API.instance().getGridManager().add(GridBlockGridFactory.ID, new GridBlockGridFactory());
        API.instance().getGridManager().add(WirelessGridGridFactory.ID, new WirelessGridGridFactory());
        API.instance().getGridManager().add(WirelessFluidGridGridFactory.ID, new WirelessFluidGridGridFactory());
        API.instance().getGridManager().add(PortableGridGridFactory.ID, new PortableGridGridFactory());
        API.instance().getGridManager().add(PortableGridBlockGridFactory.ID, new PortableGridBlockGridFactory());

        API.instance().addExternalStorageProvider(StorageType.ITEM, new ItemExternalStorageProvider());
        API.instance().addExternalStorageProvider(StorageType.FLUID, new FluidExternalStorageProvider());

        API.instance().getCraftingPreviewElementRegistry().add(ItemCraftingPreviewElement.ID, ItemCraftingPreviewElement::read);
        API.instance().getCraftingPreviewElementRegistry().add(FluidCraftingPreviewElement.ID, FluidCraftingPreviewElement::read);
        API.instance().getCraftingPreviewElementRegistry().add(ErrorCraftingPreviewElement.ID, ErrorCraftingPreviewElement::read);

        API.instance().getCraftingMonitorElementRegistry().add(ItemCraftingMonitorElement.ID, ItemCraftingMonitorElement::read);
        API.instance().getCraftingMonitorElementRegistry().add(FluidCraftingMonitorElement.ID, FluidCraftingMonitorElement::read);
        API.instance().getCraftingMonitorElementRegistry().add(ErrorCraftingMonitorElement.ID, ErrorCraftingMonitorElement::read);

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        if (CraftingTweaksIntegration.isLoaded()) {
            CraftingTweaksIntegration.register();
        }

        if (InventorySorterIntegration.isLoaded()) {
            InventorySorterIntegration.register();
        }
    }

    private INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node) {
        node.read(tag);

        return node;
    }

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
        e.getRegistry().register(new UpgradeWithEnchantedBookRecipeSerializer().setRegistryName(RS.ID, "upgrade_with_enchanted_book"));
        e.getRegistry().register(CoverRecipe.SERIALIZER.setRegistryName(new ResourceLocation(RS.ID, "cover_recipe")));
        e.getRegistry().register(HollowCoverRecipe.SERIALIZER.setRegistryName(new ResourceLocation(RS.ID, "hollow_cover_recipe")));
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new ControllerTile(NetworkType.NORMAL), RSBlocks.CONTROLLER.getBlocks()).build(null).setRegistryName(RS.ID, "controller")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new ControllerTile(NetworkType.CREATIVE), RSBlocks.CREATIVE_CONTROLLER.getBlocks()).build(null).setRegistryName(RS.ID, "creative_controller")));
        e.getRegistry().register(TileEntityType.Builder.of(CableTile::new, RSBlocks.CABLE.get()).build(null).setRegistryName(RS.ID, "cable"));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(DiskDriveTile::new, RSBlocks.DISK_DRIVE.get()).build(null).setRegistryName(RS.ID, "disk_drive")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new GridTile(GridType.NORMAL), RSBlocks.GRID.getBlocks()).build(null).setRegistryName(RS.ID, "grid")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new GridTile(GridType.CRAFTING), RSBlocks.CRAFTING_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "crafting_grid")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new GridTile(GridType.PATTERN), RSBlocks.PATTERN_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "pattern_grid")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new GridTile(GridType.FLUID), RSBlocks.FLUID_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "fluid_grid")));

        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new StorageTile(ItemStorageType.ONE_K), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.ONE_K).get()).build(null).setRegistryName(RS.ID, "1k_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new StorageTile(ItemStorageType.FOUR_K), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.FOUR_K).get()).build(null).setRegistryName(RS.ID, "4k_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new StorageTile(ItemStorageType.SIXTEEN_K), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTEEN_K).get()).build(null).setRegistryName(RS.ID, "16k_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new StorageTile(ItemStorageType.SIXTY_FOUR_K), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "64k_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new StorageTile(ItemStorageType.CREATIVE), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.CREATIVE).get()).build(null).setRegistryName(RS.ID, "creative_storage_block")));

        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new FluidStorageTile(FluidStorageType.SIXTY_FOUR_K), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.SIXTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "64k_fluid_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new FluidStorageTile(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K).get()).build(null).setRegistryName(RS.ID, "256k_fluid_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new FluidStorageTile(FluidStorageType.THOUSAND_TWENTY_FOUR_K), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.THOUSAND_TWENTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "1024k_fluid_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new FluidStorageTile(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K).get()).build(null).setRegistryName(RS.ID, "4096k_fluid_storage_block")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new FluidStorageTile(FluidStorageType.CREATIVE), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.CREATIVE).get()).build(null).setRegistryName(RS.ID, "creative_fluid_storage_block")));

        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(ExternalStorageTile::new, RSBlocks.EXTERNAL_STORAGE.get()).build(null).setRegistryName(RS.ID, "external_storage")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(ImporterTile::new, RSBlocks.IMPORTER.get()).build(null).setRegistryName(RS.ID, "importer")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(ExporterTile::new, RSBlocks.EXPORTER.get()).build(null).setRegistryName(RS.ID, "exporter")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(NetworkReceiverTile::new, RSBlocks.NETWORK_RECEIVER.getBlocks()).build(null).setRegistryName(RS.ID, "network_receiver")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(NetworkTransmitterTile::new, RSBlocks.NETWORK_TRANSMITTER.getBlocks()).build(null).setRegistryName(RS.ID, "network_transmitter")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(RelayTile::new, RSBlocks.RELAY.getBlocks()).build(null).setRegistryName(RS.ID, "relay")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(DetectorTile::new, RSBlocks.DETECTOR.getBlocks()).build(null).setRegistryName(RS.ID, "detector")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(SecurityManagerTile::new, RSBlocks.SECURITY_MANAGER.getBlocks()).build(null).setRegistryName(RS.ID, "security_manager")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(InterfaceTile::new, RSBlocks.INTERFACE.get()).build(null).setRegistryName(RS.ID, "interface")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(FluidInterfaceTile::new, RSBlocks.FLUID_INTERFACE.get()).build(null).setRegistryName(RS.ID, "fluid_interface")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(WirelessTransmitterTile::new, RSBlocks.WIRELESS_TRANSMITTER.getBlocks()).build(null).setRegistryName(RS.ID, "wireless_transmitter")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(StorageMonitorTile::new, RSBlocks.STORAGE_MONITOR.get()).build(null).setRegistryName(RS.ID, "storage_monitor")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(ConstructorTile::new, RSBlocks.CONSTRUCTOR.get()).build(null).setRegistryName(RS.ID, "constructor")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(DestructorTile::new, RSBlocks.DESTRUCTOR.get()).build(null).setRegistryName(RS.ID, "destructor")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(DiskManipulatorTile::new, RSBlocks.DISK_MANIPULATOR.getBlocks()).build(null).setRegistryName(RS.ID, "disk_manipulator")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(CrafterTile::new, RSBlocks.CRAFTER.getBlocks()).build(null).setRegistryName(RS.ID, "crafter")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(CrafterManagerTile::new, RSBlocks.CRAFTER_MANAGER.getBlocks()).build(null).setRegistryName(RS.ID, "crafter_manager")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(CraftingMonitorTile::new, RSBlocks.CRAFTING_MONITOR.getBlocks()).build(null).setRegistryName(RS.ID, "crafting_monitor")));

        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new PortableGridTile(PortableGridBlockItem.Type.CREATIVE), RSBlocks.CREATIVE_PORTABLE_GRID.get()).build(null).setRegistryName(RS.ID, "creative_portable_grid")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(() -> new PortableGridTile(PortableGridBlockItem.Type.NORMAL), RSBlocks.PORTABLE_GRID.get()).build(null).setRegistryName(RS.ID, "portable_grid")));
    }

    private <T extends TileEntity> TileEntityType<T> registerTileDataParameters(TileEntityType<T> t) {
        BaseTile tile = (BaseTile) t.create();

        tile.getDataManager().getParameters().forEach(TileDataManager::registerParameter);

        return t;
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new FilterContainer(inv.player, inv.getSelected(), windowId)).setRegistryName(RS.ID, "filter"));
        e.getRegistry().register(IForgeContainerType.create(((windowId, inv, data) -> new ControllerContainer(null, inv.player, windowId))).setRegistryName(RS.ID, "controller"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<DiskDriveContainer, DiskDriveTile>((windowId, inv, tile) -> new DiskDriveContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "disk_drive"));
        e.getRegistry().register(IForgeContainerType.create(new GridContainerFactory()).setRegistryName(RS.ID, "grid"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<StorageContainer, StorageTile>((windowId, inv, tile) -> new StorageContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "storage_block"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<FluidStorageContainer, FluidStorageTile>((windowId, inv, tile) -> new FluidStorageContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "fluid_storage_block"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<ExternalStorageContainer, ExternalStorageTile>((windowId, inv, tile) -> new ExternalStorageContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "external_storage"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<ImporterContainer, ImporterTile>((windowId, inv, tile) -> new ImporterContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "importer"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<ExporterContainer, ExporterTile>((windowId, inv, tile) -> new ExporterContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "exporter"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<NetworkTransmitterContainer, NetworkTransmitterTile>((windowId, inv, tile) -> new NetworkTransmitterContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "network_transmitter"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<RelayContainer, RelayTile>((windowId, inv, tile) -> new RelayContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "relay"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<DetectorContainer, DetectorTile>((windowId, inv, tile) -> new DetectorContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "detector"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<SecurityManagerContainer, SecurityManagerTile>((windowId, inv, tile) -> new SecurityManagerContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "security_manager"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<InterfaceContainer, InterfaceTile>((windowId, inv, tile) -> new InterfaceContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "interface"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<FluidInterfaceContainer, FluidInterfaceTile>((windowId, inv, tile) -> new FluidInterfaceContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "fluid_interface"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<WirelessTransmitterContainer, WirelessTransmitterTile>((windowId, inv, tile) -> new WirelessTransmitterContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "wireless_transmitter"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<StorageMonitorContainer, StorageMonitorTile>((windowId, inv, tile) -> new StorageMonitorContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "storage_monitor"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<ConstructorContainer, ConstructorTile>((windowId, inv, tile) -> new ConstructorContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "constructor"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<DestructorContainer, DestructorTile>((windowId, inv, tile) -> new DestructorContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "destructor"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<DiskManipulatorContainer, DiskManipulatorTile>((windowId, inv, tile) -> new DiskManipulatorContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "disk_manipulator"));
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<CrafterContainer, CrafterTile>((windowId, inv, tile) -> new CrafterContainer(tile, inv.player, windowId))).setRegistryName(RS.ID, "crafter"));
        e.getRegistry().register(IForgeContainerType.create(new CrafterManagerContainerFactory()).setRegistryName(RS.ID, "crafter_manager"));
        e.getRegistry().register(IForgeContainerType.create(new CraftingMonitorContainerFactory()).setRegistryName(RS.ID, "crafting_monitor"));
        e.getRegistry().register(IForgeContainerType.create(new WirelessCraftingMonitorContainerFactory()).setRegistryName(RS.ID, "wireless_crafting_monitor"));
    }
}
