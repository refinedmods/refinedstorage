package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSContainers {
    @ObjectHolder("controller")
    public static final MenuType<ControllerContainer> CONTROLLER = null;
    @ObjectHolder("grid")
    public static final MenuType<GridContainer> GRID = null;
    @ObjectHolder("detector")
    public static final MenuType<DetectorContainer> DETECTOR = null;
    @ObjectHolder("exporter")
    public static final MenuType<ExporterContainer> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final MenuType<ExternalStorageContainer> EXTERNAL_STORAGE = null;
    @ObjectHolder("filter")
    public static final MenuType<FilterContainer> FILTER = null;
    @ObjectHolder("importer")
    public static final MenuType<ImporterContainer> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final MenuType<NetworkTransmitterContainer> NETWORK_TRANSMITTER = null;
    @ObjectHolder("relay")
    public static final MenuType<RelayContainer> RELAY = null;
    @ObjectHolder("disk_drive")
    public static final MenuType<DiskDriveContainer> DISK_DRIVE = null;
    @ObjectHolder("storage_block")
    public static final MenuType<StorageContainer> STORAGE_BLOCK = null;
    @ObjectHolder("fluid_storage_block")
    public static final MenuType<FluidStorageContainer> FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final MenuType<SecurityManagerContainer> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final MenuType<InterfaceContainer> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final MenuType<FluidInterfaceContainer> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final MenuType<WirelessTransmitterContainer> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final MenuType<StorageMonitorContainer> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final MenuType<ConstructorContainer> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final MenuType<DestructorContainer> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final MenuType<DiskManipulatorContainer> DISK_MANIPULATOR = null;
    @ObjectHolder("crafter")
    public static final MenuType<CrafterContainer> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final MenuType<CrafterManagerContainer> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final MenuType<CraftingMonitorContainer> CRAFTING_MONITOR = null;
    @ObjectHolder("wireless_crafting_monitor")
    public static final MenuType<CraftingMonitorContainer> WIRELESS_CRAFTING_MONITOR = null;

    private RSContainers() {
    }
}
