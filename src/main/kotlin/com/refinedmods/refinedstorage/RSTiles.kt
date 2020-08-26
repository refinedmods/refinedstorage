package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.tile.*
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile
import com.refinedmods.refinedstorage.tile.grid.GridTile
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import net.minecraft.tileentity.BlockEntityType
import net.minecraftforge.registries.ObjectHolder

object RSTiles {
    @ObjectHolder("controller")
    val CONTROLLER: BlockEntityType<ControllerTile>? = null

    @ObjectHolder("creative_controller")
    val CREATIVE_CONTROLLER: BlockEntityType<ControllerTile>? = null

    @ObjectHolder("detector")
    val DETECTOR: BlockEntityType<DetectorTile>? = null

    @ObjectHolder("disk_drive")
    val DISK_DRIVE: BlockEntityType<DiskDriveTile>? = null

    @ObjectHolder("exporter")
    val EXPORTER: BlockEntityType<ExporterTile>? = null

    @ObjectHolder("external_storage")
    val EXTERNAL_STORAGE: BlockEntityType<ExternalStorageTile>? = null

    @ObjectHolder("grid")
    val GRID: BlockEntityType<GridTile>? = null

    @ObjectHolder("crafting_grid")
    val CRAFTING_GRID: BlockEntityType<GridTile>? = null

    @ObjectHolder("pattern_grid")
    val PATTERN_GRID: BlockEntityType<GridTile>? = null

    @ObjectHolder("fluid_grid")
    val FLUID_GRID: BlockEntityType<GridTile>? = null

    @ObjectHolder("importer")
    val IMPORTER: BlockEntityType<ImporterTile>? = null

    @ObjectHolder("network_transmitter")
    val NETWORK_TRANSMITTER: BlockEntityType<NetworkTransmitterTile>? = null

    @ObjectHolder("network_receiver")
    val NETWORK_RECEIVER: BlockEntityType<NetworkReceiverTile>? = null

    @ObjectHolder("relay")
    val RELAY: BlockEntityType<RelayTile>? = null

    @ObjectHolder("cable")
    val CABLE: BlockEntityType<CableTile>? = null

    @ObjectHolder("1k_storage_block")
    val ONE_K_STORAGE_BLOCK: BlockEntityType<StorageTile>? = null

    @ObjectHolder("4k_storage_block")
    val FOUR_K_STORAGE_BLOCK: BlockEntityType<StorageTile>? = null

    @ObjectHolder("16k_storage_block")
    val SIXTEEN_K_STORAGE_BLOCK: BlockEntityType<StorageTile>? = null

    @ObjectHolder("64k_storage_block")
    val SIXTY_FOUR_K_STORAGE_BLOCK: BlockEntityType<StorageTile>? = null

    @ObjectHolder("creative_storage_block")
    val CREATIVE_STORAGE_BLOCK: BlockEntityType<StorageTile>? = null

    @ObjectHolder("64k_fluid_storage_block")
    val SIXTY_FOUR_K_FLUID_STORAGE_BLOCK: BlockEntityType<FluidStorageTile>? = null

    @ObjectHolder("256k_fluid_storage_block")
    val TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK: BlockEntityType<FluidStorageTile>? = null

    @ObjectHolder("1024k_fluid_storage_block")
    val THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK: BlockEntityType<FluidStorageTile>? = null

    @ObjectHolder("4096k_fluid_storage_block")
    val FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK: BlockEntityType<FluidStorageTile>? = null

    @ObjectHolder("creative_fluid_storage_block")
    val CREATIVE_FLUID_STORAGE_BLOCK: BlockEntityType<FluidStorageTile>? = null

    @ObjectHolder("security_manager")
    val SECURITY_MANAGER: BlockEntityType<SecurityManagerTile>? = null

    @ObjectHolder("interface")
    val INTERFACE: BlockEntityType<InterfaceTile>? = null

    @ObjectHolder("fluid_interface")
    val FLUID_INTERFACE: BlockEntityType<FluidInterfaceTile>? = null

    @ObjectHolder("wireless_transmitter")
    val WIRELESS_TRANSMITTER: BlockEntityType<WirelessTransmitterTile>? = null

    @ObjectHolder("storage_monitor")
    val STORAGE_MONITOR: BlockEntityType<StorageMonitorTile>? = null

    @ObjectHolder("constructor")
    val CONSTRUCTOR: BlockEntityType<ConstructorTile>? = null

    @ObjectHolder("destructor")
    val DESTRUCTOR: BlockEntityType<DestructorTile>? = null

    @ObjectHolder("disk_manipulator")
    val DISK_MANIPULATOR: BlockEntityType<DiskManipulatorTile>? = null

    @ObjectHolder("portable_grid")
    val PORTABLE_GRID: BlockEntityType<PortableGridTile>? = null

    @ObjectHolder("creative_portable_grid")
    val CREATIVE_PORTABLE_GRID: BlockEntityType<PortableGridTile>? = null

    @ObjectHolder("crafter")
    val CRAFTER: BlockEntityType<CrafterTile>? = null

    @ObjectHolder("crafter_manager")
    val CRAFTER_MANAGER: BlockEntityType<CrafterManagerTile>? = null

    @ObjectHolder("crafting_monitor")
    val CRAFTING_MONITOR: BlockEntityType<CraftingMonitorTile>? = null
}