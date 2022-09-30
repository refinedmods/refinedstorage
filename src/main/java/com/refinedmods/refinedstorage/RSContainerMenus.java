package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.container.*;
import com.refinedmods.refinedstorage.container.factory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RSContainerMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RS.ID);

    public static final RegistryObject<MenuType<ControllerContainerMenu>> CONTROLLER = REGISTRY.register("controller", () -> IForgeMenuType.create(((windowId, inv, data) -> new ControllerContainerMenu(null, inv.player, windowId))));
    public static final RegistryObject<MenuType<GridContainerMenu>> GRID = REGISTRY.register("grid", () -> IForgeMenuType.create(new GridContainerFactory()));
    public static final RegistryObject<MenuType<DetectorContainerMenu>> DETECTOR = REGISTRY.register("detector", () -> IForgeMenuType.create(new BlockEntityContainerFactory<DetectorContainerMenu, DetectorBlockEntity>((windowId, inv, blockEntity) -> new DetectorContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<ExporterContainerMenu>> EXPORTER = REGISTRY.register("exporter", () -> IForgeMenuType.create(new BlockEntityContainerFactory<ExporterContainerMenu, ExporterBlockEntity>((windowId, inv, blockEntity) -> new ExporterContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<ExternalStorageContainerMenu>> EXTERNAL_STORAGE = REGISTRY.register("external_storage", () -> IForgeMenuType.create(new BlockEntityContainerFactory<ExternalStorageContainerMenu, ExternalStorageBlockEntity>((windowId, inv, blockEntity) -> new ExternalStorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<FilterContainerMenu>> FILTER = REGISTRY.register("filter", () -> IForgeMenuType.create((windowId, inv, data) -> new FilterContainerMenu(inv.player, inv.getSelected(), windowId)));
    public static final RegistryObject<MenuType<ImporterContainerMenu>> IMPORTER = REGISTRY.register("importer", () -> IForgeMenuType.create(new BlockEntityContainerFactory<ImporterContainerMenu, ImporterBlockEntity>((windowId, inv, blockEntity) -> new ImporterContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<NetworkTransmitterContainerMenu>> NETWORK_TRANSMITTER = REGISTRY.register("network_transmitter", () -> IForgeMenuType.create(new BlockEntityContainerFactory<NetworkTransmitterContainerMenu, NetworkTransmitterBlockEntity>((windowId, inv, blockEntity) -> new NetworkTransmitterContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<RelayContainerMenu>> RELAY = REGISTRY.register("relay", () -> IForgeMenuType.create(new BlockEntityContainerFactory<RelayContainerMenu, RelayBlockEntity>((windowId, inv, blockEntity) -> new RelayContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<DiskDriveContainerMenu>> DISK_DRIVE = REGISTRY.register("disk_drive", () -> IForgeMenuType.create(new BlockEntityContainerFactory<DiskDriveContainerMenu, DiskDriveBlockEntity>((windowId, inv, blockEntity) -> new DiskDriveContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<StorageContainerMenu>> STORAGE_BLOCK = REGISTRY.register("storage_block", () -> IForgeMenuType.create(new BlockEntityContainerFactory<StorageContainerMenu, StorageBlockEntity>((windowId, inv, blockEntity) -> new StorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<FluidStorageContainerMenu>> FLUID_STORAGE_BLOCK = REGISTRY.register("fluid_storage_block", () -> IForgeMenuType.create(new BlockEntityContainerFactory<FluidStorageContainerMenu, FluidStorageBlockEntity>((windowId, inv, blockEntity) -> new FluidStorageContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<SecurityManagerContainerMenu>> SECURITY_MANAGER = REGISTRY.register("security_manager", () -> IForgeMenuType.create(new BlockEntityContainerFactory<SecurityManagerContainerMenu, SecurityManagerBlockEntity>((windowId, inv, blockEntity) -> new SecurityManagerContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<InterfaceContainerMenu>> INTERFACE = REGISTRY.register("interface", () -> IForgeMenuType.create(new BlockEntityContainerFactory<InterfaceContainerMenu, InterfaceBlockEntity>((windowId, inv, blockEntity) -> new InterfaceContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<FluidInterfaceContainerMenu>> FLUID_INTERFACE = REGISTRY.register("fluid_interface", () -> IForgeMenuType.create(new BlockEntityContainerFactory<FluidInterfaceContainerMenu, FluidInterfaceBlockEntity>((windowId, inv, blockEntity) -> new FluidInterfaceContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<WirelessTransmitterContainerMenu>> WIRELESS_TRANSMITTER = REGISTRY.register("wireless_transmitter", () -> IForgeMenuType.create(new BlockEntityContainerFactory<WirelessTransmitterContainerMenu, WirelessTransmitterBlockEntity>((windowId, inv, blockEntity) -> new WirelessTransmitterContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<StorageMonitorContainerMenu>> STORAGE_MONITOR = REGISTRY.register("storage_monitor", () -> IForgeMenuType.create(new BlockEntityContainerFactory<StorageMonitorContainerMenu, StorageMonitorBlockEntity>((windowId, inv, blockEntity) -> new StorageMonitorContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<ConstructorContainerMenu>> CONSTRUCTOR = REGISTRY.register("constructor", () -> IForgeMenuType.create(new BlockEntityContainerFactory<ConstructorContainerMenu, ConstructorBlockEntity>((windowId, inv, blockEntity) -> new ConstructorContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<DestructorContainerMenu>> DESTRUCTOR = REGISTRY.register("destructor", () -> IForgeMenuType.create(new BlockEntityContainerFactory<DestructorContainerMenu, DestructorBlockEntity>((windowId, inv, blockEntity) -> new DestructorContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<DiskManipulatorContainerMenu>> DISK_MANIPULATOR = REGISTRY.register("disk_manipulator", () -> IForgeMenuType.create(new BlockEntityContainerFactory<DiskManipulatorContainerMenu, DiskManipulatorBlockEntity>((windowId, inv, blockEntity) -> new DiskManipulatorContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<CrafterContainerMenu>> CRAFTER = REGISTRY.register("crafter", () -> IForgeMenuType.create(new BlockEntityContainerFactory<CrafterContainerMenu, CrafterBlockEntity>((windowId, inv, blockEntity) -> new CrafterContainerMenu(blockEntity, inv.player, windowId))));
    public static final RegistryObject<MenuType<CrafterManagerContainerMenu>> CRAFTER_MANAGER = REGISTRY.register("crafter_manager", () -> IForgeMenuType.create(new CrafterManagerContainerFactory()));
    public static final RegistryObject<MenuType<CraftingMonitorContainerMenu>> CRAFTING_MONITOR = REGISTRY.register("crafting_monitor", () -> IForgeMenuType.create(new CraftingMonitorContainerFactory()));
    public static final RegistryObject<MenuType<CraftingMonitorContainerMenu>> WIRELESS_CRAFTING_MONITOR = REGISTRY.register("wireless_crafting_monitor", () -> IForgeMenuType.create(new WirelessCraftingMonitorContainerFactory()));

    private RSContainerMenus() {
    }
}
