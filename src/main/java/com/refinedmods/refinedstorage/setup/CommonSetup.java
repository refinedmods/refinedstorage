package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.RSCreativeModeTabItems;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSLootFunctions;
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
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.GridBlockGridFactory;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridBlockGridFactory;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.WirelessFluidGridGridFactory;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.WirelessGridGridFactory;
import com.refinedmods.refinedstorage.apiimpl.network.node.CableNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.InterfaceNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkTransmitterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.RelayNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
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
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration;
import com.refinedmods.refinedstorage.integration.inventorysorter.InventorySorterIntegration;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class CommonSetup {
    private CommonSetup() {
    }

    @SubscribeEvent
    public static void onRegister(final RegisterEvent e) {
        e.register(Registries.LOOT_FUNCTION_TYPE, helper -> RSLootFunctions.register());
        e.register(Registries.CREATIVE_MODE_TAB, RSCreativeModeTabItems::register);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent e) {
        NeoForge.EVENT_BUS.register(new NetworkNodeListener());
        NeoForge.EVENT_BUS.register(new NetworkListener());
        NeoForge.EVENT_BUS.register(new BlockListener());

        API.instance().getStorageDiskRegistry().add(ItemStorageDiskFactory.ID, new ItemStorageDiskFactory());
        API.instance().getStorageDiskRegistry().add(FluidStorageDiskFactory.ID, new FluidStorageDiskFactory());

        API.instance().getNetworkNodeRegistry().add(DiskDriveNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new DiskDriveNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(CableNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CableNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.NORMAL)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.CRAFTING_ID,
            (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.CRAFTING)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.PATTERN_ID,
            (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.PATTERN)));
        API.instance().getNetworkNodeRegistry().add(GridNetworkNode.FLUID_ID,
            (tag, world, pos) -> readAndReturn(tag, new GridNetworkNode(world, pos, GridType.FLUID)));

        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.ONE_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.SIXTEEN_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.SIXTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry().add(StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageNetworkNode(world, pos, ItemStorageType.CREATIVE)));

        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag,
                new FluidStorageNetworkNode(world, pos, FluidStorageType.SIXTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry()
            .add(FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID,
                (tag, world, pos) -> readAndReturn(tag,
                    new FluidStorageNetworkNode(world, pos, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K)));
        API.instance().getNetworkNodeRegistry()
            .add(FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID,
                (tag, world, pos) -> readAndReturn(tag,
                    new FluidStorageNetworkNode(world, pos, FluidStorageType.THOUSAND_TWENTY_FOUR_K)));
        API.instance().getNetworkNodeRegistry()
            .add(FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID,
                (tag, world, pos) -> readAndReturn(tag,
                    new FluidStorageNetworkNode(world, pos, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K)));
        API.instance().getNetworkNodeRegistry().add(FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID,
            (tag, world, pos) -> readAndReturn(tag,
                new FluidStorageNetworkNode(world, pos, FluidStorageType.CREATIVE)));

        API.instance().getNetworkNodeRegistry().add(ExternalStorageNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new ExternalStorageNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(ImporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ImporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(ExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new ExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(NetworkReceiverNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new NetworkReceiverNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(NetworkTransmitterNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new NetworkTransmitterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(RelayNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new RelayNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(DetectorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new DetectorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(SecurityManagerNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new SecurityManagerNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(InterfaceNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new InterfaceNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(FluidInterfaceNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new FluidInterfaceNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(WirelessTransmitterNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new WirelessTransmitterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(StorageMonitorNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new StorageMonitorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(ConstructorNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new ConstructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(DestructorNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new DestructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(DiskManipulatorNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new DiskManipulatorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry()
            .add(CrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CrafterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CrafterManagerNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new CrafterManagerNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CraftingMonitorNetworkNode.ID,
            (tag, world, pos) -> readAndReturn(tag, new CraftingMonitorNetworkNode(world, pos)));

        API.instance().getGridManager().add(GridBlockGridFactory.ID, new GridBlockGridFactory());
        API.instance().getGridManager().add(WirelessGridGridFactory.ID, new WirelessGridGridFactory());
        API.instance().getGridManager().add(WirelessFluidGridGridFactory.ID, new WirelessFluidGridGridFactory());
        API.instance().getGridManager().add(PortableGridGridFactory.ID, new PortableGridGridFactory());
        API.instance().getGridManager().add(PortableGridBlockGridFactory.ID, new PortableGridBlockGridFactory());

        API.instance().addExternalStorageProvider(StorageType.ITEM, new ItemExternalStorageProvider());
        API.instance().addExternalStorageProvider(StorageType.FLUID, new FluidExternalStorageProvider());

        API.instance().getCraftingPreviewElementRegistry()
            .add(ItemCraftingPreviewElement.ID, ItemCraftingPreviewElement::read);
        API.instance().getCraftingPreviewElementRegistry()
            .add(FluidCraftingPreviewElement.ID, FluidCraftingPreviewElement::read);
        API.instance().getCraftingPreviewElementRegistry()
            .add(ErrorCraftingPreviewElement.ID, ErrorCraftingPreviewElement::read);

        API.instance().getCraftingMonitorElementRegistry()
            .add(ItemCraftingMonitorElement.ID, ItemCraftingMonitorElement::read);
        API.instance().getCraftingMonitorElementRegistry()
            .add(FluidCraftingMonitorElement.ID, FluidCraftingMonitorElement::read);
        API.instance().getCraftingMonitorElementRegistry()
            .add(ErrorCraftingMonitorElement.ID, ErrorCraftingMonitorElement::read);

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        if (CraftingTweaksIntegration.isLoaded()) {
            CraftingTweaksIntegration.register();
        }

        if (InventorySorterIntegration.isLoaded()) {
            InventorySorterIntegration.register();
        }
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);

        return node;
    }

    @SubscribeEvent
    public static void onRegisterNetworkPackets(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(RS.ID);
        RS.NETWORK_HANDLER.register(registrar);
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent e) {
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.CRAFTER.get(),
            CrafterBlockEntity::getPatterns
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.DISK_MANIPULATOR.get(),
            (be, side) -> be.getNode().getDisks()
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.FLUID_INTERFACE.get(),
            (be, side) -> be.getNode().getIn()
        );
        e.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            RSBlockEntities.FLUID_INTERFACE.get(),
            (be, side) -> be.getNode().getTank()
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.INTERFACE.get(),
            (be, side) -> be.getNode().getItems()
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.DISK_DRIVE.get(),
            (be, side) -> be.getNode().getDisks()
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.NETWORK_TRANSMITTER.get(),
            (be, side) -> be.getNode().getNetworkCard()
        );
        e.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            RSBlockEntities.CONTROLLER.get(),
            (be, side) -> be.getNetwork().getEnergyStorage()
        );
        e.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            RSBlockEntities.GRID.get(),
            (be, side) -> be.getInventory()
        );
        e.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            RSBlockEntities.PORTABLE_GRID.get(),
            (be, side) -> be.getEnergyStorage()
        );
        RSItems.CONTROLLER.values().forEach(value -> e.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> value.get().createEnergyStorage(stack),
            value.get()
        ));
        e.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> RSItems.PORTABLE_GRID.get().createEnergyStorage(stack),
            RSItems.PORTABLE_GRID.get()
        );
        e.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> RSItems.WIRELESS_GRID.get().createEnergyStorage(stack),
            RSItems.WIRELESS_GRID.get()
        );
        e.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> RSItems.WIRELESS_FLUID_GRID.get().createEnergyStorage(stack),
            RSItems.WIRELESS_FLUID_GRID.get()
        );
        e.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> RSItems.WIRELESS_CRAFTING_MONITOR.get().createEnergyStorage(stack),
            RSItems.WIRELESS_CRAFTING_MONITOR.get()
        );
    }
}
