package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
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
import com.refinedmods.refinedstorage.container.*;
import com.refinedmods.refinedstorage.container.factory.*;
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration;
import com.refinedmods.refinedstorage.integration.inventorysorter.InventorySorterIntegration;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import com.refinedmods.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer;
import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup {
    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent e) {
        RS.NETWORK_HANDLER.register();

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

    private INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);

        return node;
    }

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent e) {
        e.register(INetworkNodeProxy.class);
    }

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> e) {
        e.getRegistry().register(new UpgradeWithEnchantedBookRecipeSerializer().setRegistryName(RS.ID, "upgrade_with_enchanted_book"));
        e.getRegistry().register(CoverRecipe.SERIALIZER.setRegistryName(new ResourceLocation(RS.ID, "cover_recipe")));
        e.getRegistry().register(HollowCoverRecipe.SERIALIZER.setRegistryName(new ResourceLocation(RS.ID, "hollow_cover_recipe")));
    }

    @SubscribeEvent
    public void onRegisterBlockEntities(RegistryEvent.Register<BlockEntityType<?>> e) {
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new ControllerBlockEntity(NetworkType.NORMAL, pos, state), RSBlocks.CONTROLLER.getBlocks()).build(null).setRegistryName(RS.ID, "controller")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new ControllerBlockEntity(NetworkType.CREATIVE, pos, state), RSBlocks.CREATIVE_CONTROLLER.getBlocks()).build(null).setRegistryName(RS.ID, "creative_controller")));
        e.getRegistry().register(BlockEntityType.Builder.of(CableBlockEntity::new, RSBlocks.CABLE.get()).build(null).setRegistryName(RS.ID, "cable"));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(DiskDriveBlockEntity::new, RSBlocks.DISK_DRIVE.get()).build(null).setRegistryName(RS.ID, "disk_drive")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.NORMAL, pos, state), RSBlocks.GRID.getBlocks()).build(null).setRegistryName(RS.ID, "grid")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.CRAFTING, pos, state), RSBlocks.CRAFTING_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "crafting_grid")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.PATTERN, pos, state), RSBlocks.PATTERN_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "pattern_grid")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.FLUID, pos, state), RSBlocks.FLUID_GRID.getBlocks()).build(null).setRegistryName(RS.ID, "fluid_grid")));

        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.ONE_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.ONE_K).get()).build(null).setRegistryName(RS.ID, "1k_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.FOUR_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.FOUR_K).get()).build(null).setRegistryName(RS.ID, "4k_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.SIXTEEN_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTEEN_K).get()).build(null).setRegistryName(RS.ID, "16k_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.SIXTY_FOUR_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "64k_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.CREATIVE, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.CREATIVE).get()).build(null).setRegistryName(RS.ID, "creative_storage_block")));

        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.SIXTY_FOUR_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.SIXTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "64k_fluid_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K).get()).build(null).setRegistryName(RS.ID, "256k_fluid_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.THOUSAND_TWENTY_FOUR_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.THOUSAND_TWENTY_FOUR_K).get()).build(null).setRegistryName(RS.ID, "1024k_fluid_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K).get()).build(null).setRegistryName(RS.ID, "4096k_fluid_storage_block")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.CREATIVE, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.CREATIVE).get()).build(null).setRegistryName(RS.ID, "creative_fluid_storage_block")));

        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(ExternalStorageBlockEntity::new, RSBlocks.EXTERNAL_STORAGE.get()).build(null).setRegistryName(RS.ID, "external_storage")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(ImporterBlockEntity::new, RSBlocks.IMPORTER.get()).build(null).setRegistryName(RS.ID, "importer")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(ExporterBlockEntity::new, RSBlocks.EXPORTER.get()).build(null).setRegistryName(RS.ID, "exporter")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(NetworkReceiverBlockEntity::new, RSBlocks.NETWORK_RECEIVER.getBlocks()).build(null).setRegistryName(RS.ID, "network_receiver")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(NetworkTransmitterBlockEntity::new, RSBlocks.NETWORK_TRANSMITTER.getBlocks()).build(null).setRegistryName(RS.ID, "network_transmitter")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(RelayBlockEntity::new, RSBlocks.RELAY.getBlocks()).build(null).setRegistryName(RS.ID, "relay")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(DetectorBlockEntity::new, RSBlocks.DETECTOR.getBlocks()).build(null).setRegistryName(RS.ID, "detector")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(SecurityManagerBlockEntity::new, RSBlocks.SECURITY_MANAGER.getBlocks()).build(null).setRegistryName(RS.ID, "security_manager")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(InterfaceBlockEntity::new, RSBlocks.INTERFACE.get()).build(null).setRegistryName(RS.ID, "interface")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(FluidInterfaceBlockEntity::new, RSBlocks.FLUID_INTERFACE.get()).build(null).setRegistryName(RS.ID, "fluid_interface")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(WirelessTransmitterBlockEntity::new, RSBlocks.WIRELESS_TRANSMITTER.getBlocks()).build(null).setRegistryName(RS.ID, "wireless_transmitter")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(StorageMonitorBlockEntity::new, RSBlocks.STORAGE_MONITOR.get()).build(null).setRegistryName(RS.ID, "storage_monitor")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(ConstructorBlockEntity::new, RSBlocks.CONSTRUCTOR.get()).build(null).setRegistryName(RS.ID, "constructor")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(DestructorBlockEntity::new, RSBlocks.DESTRUCTOR.get()).build(null).setRegistryName(RS.ID, "destructor")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(DiskManipulatorBlockEntity::new, RSBlocks.DISK_MANIPULATOR.getBlocks()).build(null).setRegistryName(RS.ID, "disk_manipulator")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(CrafterBlockEntity::new, RSBlocks.CRAFTER.getBlocks()).build(null).setRegistryName(RS.ID, "crafter")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(CrafterManagerBlockEntity::new, RSBlocks.CRAFTER_MANAGER.getBlocks()).build(null).setRegistryName(RS.ID, "crafter_manager")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(CraftingMonitorBlockEntity::new, RSBlocks.CRAFTING_MONITOR.getBlocks()).build(null).setRegistryName(RS.ID, "crafting_monitor")));

        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new PortableGridBlockEntity(PortableGridBlockItem.Type.CREATIVE, pos, state), RSBlocks.CREATIVE_PORTABLE_GRID.get()).build(null).setRegistryName(RS.ID, "creative_portable_grid")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of((pos, state) -> new PortableGridBlockEntity(PortableGridBlockItem.Type.NORMAL, pos, state), RSBlocks.PORTABLE_GRID.get()).build(null).setRegistryName(RS.ID, "portable_grid")));
    }

    private <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntityType<T> t) {
        BaseBlockEntity blockEntity = (BaseBlockEntity) t.create(BlockPos.ZERO, null);

        blockEntity.getDataManager().getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);

        return t;
    }

    @SubscribeEvent
    public void onRegisterMenus(RegistryEvent.Register<MenuType<?>> e) {
        e.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new FilterContainerMenu(inv.player, inv.getSelected(), windowId)).setRegistryName(RS.ID, "filter"));
        e.getRegistry().register(IForgeMenuType.create(((windowId, inv, data) -> new ControllerContainerMenu(null, inv.player, windowId))).setRegistryName(RS.ID, "controller"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<DiskDriveContainerMenu, DiskDriveBlockEntity>((windowId, inv, blockEntity) -> new DiskDriveContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "disk_drive"));
        e.getRegistry().register(IForgeMenuType.create(new GridContainerFactory()).setRegistryName(RS.ID, "grid"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<StorageContainerMenu, StorageBlockEntity>((windowId, inv, blockEntity) -> new StorageContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "storage_block"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<FluidStorageContainerMenu, FluidStorageBlockEntity>((windowId, inv, blockEntity) -> new FluidStorageContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "fluid_storage_block"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<ExternalStorageContainerMenu, ExternalStorageBlockEntity>((windowId, inv, blockEntity) -> new ExternalStorageContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "external_storage"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<ImporterContainerMenu, ImporterBlockEntity>((windowId, inv, blockEntity) -> new ImporterContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "importer"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<ExporterContainerMenu, ExporterBlockEntity>((windowId, inv, blockEntity) -> new ExporterContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "exporter"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<NetworkTransmitterContainerMenu, NetworkTransmitterBlockEntity>((windowId, inv, blockEntity) -> new NetworkTransmitterContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "network_transmitter"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<RelayContainerMenu, RelayBlockEntity>((windowId, inv, blockEntity) -> new RelayContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "relay"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<DetectorContainerMenu, DetectorBlockEntity>((windowId, inv, blockEntity) -> new DetectorContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "detector"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<SecurityManagerContainerMenu, SecurityManagerBlockEntity>((windowId, inv, blockEntity) -> new SecurityManagerContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "security_manager"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<InterfaceContainerMenu, InterfaceBlockEntity>((windowId, inv, blockEntity) -> new InterfaceContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "interface"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<FluidInterfaceContainerMenu, FluidInterfaceBlockEntity>((windowId, inv, blockEntity) -> new FluidInterfaceContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "fluid_interface"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<WirelessTransmitterContainerMenu, WirelessTransmitterBlockEntity>((windowId, inv, blockEntity) -> new WirelessTransmitterContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "wireless_transmitter"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<StorageMonitorContainerMenu, StorageMonitorBlockEntity>((windowId, inv, blockEntity) -> new StorageMonitorContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "storage_monitor"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<ConstructorContainerMenu, ConstructorBlockEntity>((windowId, inv, blockEntity) -> new ConstructorContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "constructor"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<DestructorContainerMenu, DestructorBlockEntity>((windowId, inv, blockEntity) -> new DestructorContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "destructor"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<DiskManipulatorContainerMenu, DiskManipulatorBlockEntity>((windowId, inv, blockEntity) -> new DiskManipulatorContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "disk_manipulator"));
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<CrafterContainerMenu, CrafterBlockEntity>((windowId, inv, blockEntity) -> new CrafterContainerMenu(blockEntity, inv.player, windowId))).setRegistryName(RS.ID, "crafter"));
        e.getRegistry().register(IForgeMenuType.create(new CrafterManagerContainerFactory()).setRegistryName(RS.ID, "crafter_manager"));
        e.getRegistry().register(IForgeMenuType.create(new CraftingMonitorContainerFactory()).setRegistryName(RS.ID, "crafting_monitor"));
        e.getRegistry().register(IForgeMenuType.create(new WirelessCraftingMonitorContainerFactory()).setRegistryName(RS.ID, "wireless_crafting_monitor"));
    }
}
