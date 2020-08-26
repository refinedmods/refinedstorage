package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.container.*
import net.minecraft.inventory.container.ContainerType
import net.minecraftforge.registries.ObjectHolder

object RSContainers {
    @ObjectHolder("controller")
    val CONTROLLER: ContainerType<ControllerContainer>? = null

    @ObjectHolder("grid")
    val GRID: ContainerType<GridContainer>? = null

    @ObjectHolder("detector")
    val DETECTOR: ContainerType<DetectorContainer>? = null

    @ObjectHolder("exporter")
    val EXPORTER: ContainerType<ExporterContainer>? = null

    @ObjectHolder("external_storage")
    val EXTERNAL_STORAGE: ContainerType<ExternalStorageContainer>? = null

    @ObjectHolder("filter")
    val FILTER: ContainerType<FilterContainer>? = null

    @ObjectHolder("importer")
    val IMPORTER: ContainerType<ImporterContainer>? = null

    @ObjectHolder("network_transmitter")
    val NETWORK_TRANSMITTER: ContainerType<NetworkTransmitterContainer>? = null

    @ObjectHolder("relay")
    val RELAY: ContainerType<RelayContainer>? = null

    @ObjectHolder("disk_drive")
    val DISK_DRIVE: ContainerType<DiskDriveContainer>? = null

    @ObjectHolder("storage_block")
    val STORAGE_BLOCK: ContainerType<StorageContainer>? = null

    @ObjectHolder("fluid_storage_block")
    val FLUID_STORAGE_BLOCK: ContainerType<FluidStorageContainer>? = null

    @ObjectHolder("security_manager")
    val SECURITY_MANAGER: ContainerType<SecurityManagerContainer>? = null

    @ObjectHolder("interface")
    val INTERFACE: ContainerType<InterfaceContainer>? = null

    @ObjectHolder("fluid_interface")
    val FLUID_INTERFACE: ContainerType<FluidInterfaceContainer>? = null

    @ObjectHolder("wireless_transmitter")
    val WIRELESS_TRANSMITTER: ContainerType<WirelessTransmitterContainer>? = null

    @ObjectHolder("storage_monitor")
    val STORAGE_MONITOR: ContainerType<StorageMonitorContainer>? = null

    @ObjectHolder("constructor")
    val CONSTRUCTOR: ContainerType<ConstructorContainer>? = null

    @ObjectHolder("destructor")
    val DESTRUCTOR: ContainerType<DestructorContainer>? = null

    @ObjectHolder("disk_manipulator")
    val DISK_MANIPULATOR: ContainerType<DiskManipulatorContainer>? = null

    @ObjectHolder("crafter")
    val CRAFTER: ContainerType<CrafterContainer>? = null

    @ObjectHolder("crafter_manager")
    val CRAFTER_MANAGER: ContainerType<CrafterManagerContainer>? = null

    @ObjectHolder("crafting_monitor")
    val CRAFTING_MONITOR: ContainerType<CraftingMonitorContainer>? = null

    @ObjectHolder("wireless_crafting_monitor")
    val WIRELESS_CRAFTING_MONITOR: ContainerType<CraftingMonitorContainer>? = null
}