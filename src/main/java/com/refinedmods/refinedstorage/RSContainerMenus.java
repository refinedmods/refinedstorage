package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.container.*;
import com.refinedmods.refinedstorage.container.factory.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RSContainerMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, RS.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ControllerContainerMenu>>
        CONTROLLER = REGISTRY.register("controller", () -> IMenuTypeExtension.create(((windowId, inv, data) -> new ControllerContainerMenu(null, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<GridContainerMenu>> GRID = REGISTRY.register("grid", () -> IMenuTypeExtension.create(new GridContainerFactory()));
    public static final DeferredHolder<MenuType<?>, MenuType<DetectorContainerMenu>> DETECTOR = REGISTRY.register("detector", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<DetectorContainerMenu, DetectorBlockEntity>((windowId, inv, blockEntity) -> new DetectorContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<ExporterContainerMenu>> EXPORTER = REGISTRY.register("exporter", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<ExporterContainerMenu, ExporterBlockEntity>((windowId, inv, blockEntity) -> new ExporterContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<ExternalStorageContainerMenu>> EXTERNAL_STORAGE = REGISTRY.register("external_storage", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<ExternalStorageContainerMenu, ExternalStorageBlockEntity>((windowId, inv, blockEntity) -> new ExternalStorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<FilterContainerMenu>> FILTER = REGISTRY.register("filter", () -> IMenuTypeExtension.create((windowId, inv, data) -> new FilterContainerMenu(inv.player, inv.getSelected(), windowId)));
    public static final DeferredHolder<MenuType<?>, MenuType<ImporterContainerMenu>> IMPORTER = REGISTRY.register("importer", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<ImporterContainerMenu, ImporterBlockEntity>((windowId, inv, blockEntity) -> new ImporterContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<NetworkTransmitterContainerMenu>> NETWORK_TRANSMITTER = REGISTRY.register("network_transmitter", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<NetworkTransmitterContainerMenu, NetworkTransmitterBlockEntity>((windowId, inv, blockEntity) -> new NetworkTransmitterContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<RelayContainerMenu>> RELAY = REGISTRY.register("relay", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<RelayContainerMenu, RelayBlockEntity>((windowId, inv, blockEntity) -> new RelayContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<DiskDriveContainerMenu>> DISK_DRIVE = REGISTRY.register("disk_drive", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<DiskDriveContainerMenu, DiskDriveBlockEntity>((windowId, inv, blockEntity) -> new DiskDriveContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<StorageContainerMenu>> STORAGE_BLOCK = REGISTRY.register("storage_block", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<StorageContainerMenu, StorageBlockEntity>((windowId, inv, blockEntity) -> new StorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<FluidStorageContainerMenu>> FLUID_STORAGE_BLOCK = REGISTRY.register("fluid_storage_block", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<FluidStorageContainerMenu, FluidStorageBlockEntity>((windowId, inv, blockEntity) -> new FluidStorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<SecurityManagerContainerMenu>> SECURITY_MANAGER = REGISTRY.register("security_manager", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<SecurityManagerContainerMenu, SecurityManagerBlockEntity>((windowId, inv, blockEntity) -> new SecurityManagerContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<InterfaceContainerMenu>> INTERFACE = REGISTRY.register("interface", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<InterfaceContainerMenu, InterfaceBlockEntity>((windowId, inv, blockEntity) -> new InterfaceContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<FluidInterfaceContainerMenu>> FLUID_INTERFACE = REGISTRY.register("fluid_interface", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<FluidInterfaceContainerMenu, FluidInterfaceBlockEntity>((windowId, inv, blockEntity) -> new FluidInterfaceContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<WirelessTransmitterContainerMenu>> WIRELESS_TRANSMITTER = REGISTRY.register("wireless_transmitter", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<WirelessTransmitterContainerMenu, WirelessTransmitterBlockEntity>((windowId, inv, blockEntity) -> new WirelessTransmitterContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<StorageMonitorContainerMenu>> STORAGE_MONITOR = REGISTRY.register("storage_monitor", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<StorageMonitorContainerMenu, StorageMonitorBlockEntity>((windowId, inv, blockEntity) -> new StorageMonitorContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<ConstructorContainerMenu>> CONSTRUCTOR = REGISTRY.register("constructor", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<ConstructorContainerMenu, ConstructorBlockEntity>((windowId, inv, blockEntity) -> new ConstructorContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<DestructorContainerMenu>> DESTRUCTOR = REGISTRY.register("destructor", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<DestructorContainerMenu, DestructorBlockEntity>((windowId, inv, blockEntity) -> new DestructorContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<DiskManipulatorContainerMenu>> DISK_MANIPULATOR = REGISTRY.register("disk_manipulator", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<DiskManipulatorContainerMenu, DiskManipulatorBlockEntity>((windowId, inv, blockEntity) -> new DiskManipulatorContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrafterContainerMenu>> CRAFTER = REGISTRY.register("crafter", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<CrafterContainerMenu, CrafterBlockEntity>((windowId, inv, blockEntity) -> new CrafterContainerMenu(blockEntity, inv.player, windowId))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrafterManagerContainerMenu>> CRAFTER_MANAGER = REGISTRY.register("crafter_manager", () -> IMenuTypeExtension.create(new CrafterManagerContainerFactory()));
    public static final DeferredHolder<MenuType<?>, MenuType<CraftingMonitorContainerMenu>> CRAFTING_MONITOR = REGISTRY.register("crafting_monitor", () -> IMenuTypeExtension.create(new CraftingMonitorContainerFactory()));
    public static final DeferredHolder<MenuType<?>, MenuType<CraftingMonitorContainerMenu>> WIRELESS_CRAFTING_MONITOR = REGISTRY.register("wireless_crafting_monitor", () -> IMenuTypeExtension.create(new WirelessCraftingMonitorContainerFactory()));

    private RSContainerMenus() {
    }
}
