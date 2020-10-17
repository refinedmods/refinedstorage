package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.container.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSContainers {
    @ObjectHolder("controller")
    public static final ContainerType<ControllerContainer> CONTROLLER = null;
    @ObjectHolder("grid")
    public static final ContainerType<GridContainer> GRID = null;
    @ObjectHolder("detector")
    public static final ContainerType<DetectorContainer> DETECTOR = null;
    @ObjectHolder("exporter")
    public static final ContainerType<ExporterContainer> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final ContainerType<ExternalStorageContainer> EXTERNAL_STORAGE = null;
    @ObjectHolder("filter")
    public static final ContainerType<FilterContainer> FILTER = null;
    @ObjectHolder("importer")
    public static final ContainerType<ImporterContainer> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final ContainerType<NetworkTransmitterContainer> NETWORK_TRANSMITTER = null;
    @ObjectHolder("relay")
    public static final ContainerType<RelayContainer> RELAY = null;
    @ObjectHolder("disk_drive")
    public static final ContainerType<DiskDriveContainer> DISK_DRIVE = null;
    @ObjectHolder("storage_block")
    public static final ContainerType<StorageContainer> STORAGE_BLOCK = null;
    @ObjectHolder("fluid_storage_block")
    public static final ContainerType<FluidStorageContainer> FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final ContainerType<SecurityManagerContainer> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final ContainerType<InterfaceContainer> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final ContainerType<FluidInterfaceContainer> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final ContainerType<WirelessTransmitterContainer> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final ContainerType<StorageMonitorContainer> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final ContainerType<ConstructorContainer> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final ContainerType<DestructorContainer> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final ContainerType<DiskManipulatorContainer> DISK_MANIPULATOR = null;
    @ObjectHolder("crafter")
    public static final ContainerType<CrafterContainer> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final ContainerType<CrafterManagerContainer> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final ContainerType<CraftingMonitorContainer> CRAFTING_MONITOR = null;
    @ObjectHolder("wireless_crafting_monitor")
    public static final ContainerType<CraftingMonitorContainer> WIRELESS_CRAFTING_MONITOR = null;

    private RSContainers() {
    }
}
