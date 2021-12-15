package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSContainerMenus {
    @ObjectHolder("controller")
    public static final MenuType<ControllerContainerMenu> CONTROLLER = null;
    @ObjectHolder("grid")
    public static final MenuType<GridContainerMenu> GRID = null;
    @ObjectHolder("detector")
    public static final MenuType<DetectorContainerMenu> DETECTOR = null;
    @ObjectHolder("exporter")
    public static final MenuType<ExporterContainerMenu> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final MenuType<ExternalStorageContainerMenu> EXTERNAL_STORAGE = null;
    @ObjectHolder("filter")
    public static final MenuType<FilterContainerMenu> FILTER = null;
    @ObjectHolder("importer")
    public static final MenuType<ImporterContainerMenu> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final MenuType<NetworkTransmitterContainerMenu> NETWORK_TRANSMITTER = null;
    @ObjectHolder("relay")
    public static final MenuType<RelayContainerMenu> RELAY = null;
    @ObjectHolder("disk_drive")
    public static final MenuType<DiskDriveContainerMenu> DISK_DRIVE = null;
    @ObjectHolder("storage_block")
    public static final MenuType<StorageContainerMenu> STORAGE_BLOCK = null;
    @ObjectHolder("fluid_storage_block")
    public static final MenuType<FluidStorageContainerMenu> FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final MenuType<SecurityManagerContainerMenu> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final MenuType<InterfaceContainerMenu> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final MenuType<FluidInterfaceContainerMenu> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final MenuType<WirelessTransmitterContainerMenu> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final MenuType<StorageMonitorContainerMenu> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final MenuType<ConstructorContainerMenu> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final MenuType<DestructorContainerMenu> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final MenuType<DiskManipulatorContainerMenu> DISK_MANIPULATOR = null;
    @ObjectHolder("crafter")
    public static final MenuType<CrafterContainerMenu> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final MenuType<CrafterManagerContainerMenu> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final MenuType<CraftingMonitorContainerMenu> CRAFTING_MONITOR = null;
    @ObjectHolder("wireless_crafting_monitor")
    public static final MenuType<CraftingMonitorContainerMenu> WIRELESS_CRAFTING_MONITOR = null;

    private RSContainerMenus() {
    }
}
