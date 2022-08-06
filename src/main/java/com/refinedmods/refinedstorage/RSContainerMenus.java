package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public final class RSContainerMenus {
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:controller")
    public static final MenuType<ControllerContainerMenu> CONTROLLER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:grid")
    public static final MenuType<GridContainerMenu> GRID = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:detector")
    public static final MenuType<DetectorContainerMenu> DETECTOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:exporter")
    public static final MenuType<ExporterContainerMenu> EXPORTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:external_storage")
    public static final MenuType<ExternalStorageContainerMenu> EXTERNAL_STORAGE = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:filter")
    public static final MenuType<FilterContainerMenu> FILTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:importer")
    public static final MenuType<ImporterContainerMenu> IMPORTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:network_transmitter")
    public static final MenuType<NetworkTransmitterContainerMenu> NETWORK_TRANSMITTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:relay")
    public static final MenuType<RelayContainerMenu> RELAY = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:disk_drive")
    public static final MenuType<DiskDriveContainerMenu> DISK_DRIVE = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:storage_block")
    public static final MenuType<StorageContainerMenu> STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:fluid_storage_block")
    public static final MenuType<FluidStorageContainerMenu> FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:security_manager")
    public static final MenuType<SecurityManagerContainerMenu> SECURITY_MANAGER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:interface")
    public static final MenuType<InterfaceContainerMenu> INTERFACE = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:fluid_interface")
    public static final MenuType<FluidInterfaceContainerMenu> FLUID_INTERFACE = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:wireless_transmitter")
    public static final MenuType<WirelessTransmitterContainerMenu> WIRELESS_TRANSMITTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:storage_monitor")
    public static final MenuType<StorageMonitorContainerMenu> STORAGE_MONITOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:constructor")
    public static final MenuType<ConstructorContainerMenu> CONSTRUCTOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:destructor")
    public static final MenuType<DestructorContainerMenu> DESTRUCTOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:disk_manipulator")
    public static final MenuType<DiskManipulatorContainerMenu> DISK_MANIPULATOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:crafter")
    public static final MenuType<CrafterContainerMenu> CRAFTER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:crafter_manager")
    public static final MenuType<CrafterManagerContainerMenu> CRAFTER_MANAGER = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:crafting_monitor")
    public static final MenuType<CraftingMonitorContainerMenu> CRAFTING_MONITOR = null;
    @ObjectHolder(registryName = "minecraft:menu", value = "refinedstorage:wireless_crafting_monitor")
    public static final MenuType<CraftingMonitorContainerMenu> WIRELESS_CRAFTING_MONITOR = null;

    private RSContainerMenus() {
    }
}
