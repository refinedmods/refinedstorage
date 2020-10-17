package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.tile.*;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSTiles {
    @ObjectHolder("controller")
    public static final TileEntityType<ControllerTile> CONTROLLER = null;
    @ObjectHolder("creative_controller")
    public static final TileEntityType<ControllerTile> CREATIVE_CONTROLLER = null;
    @ObjectHolder("detector")
    public static final TileEntityType<DetectorTile> DETECTOR = null;
    @ObjectHolder("disk_drive")
    public static final TileEntityType<DiskDriveTile> DISK_DRIVE = null;
    @ObjectHolder("exporter")
    public static final TileEntityType<ExporterTile> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final TileEntityType<ExternalStorageTile> EXTERNAL_STORAGE = null;
    @ObjectHolder("grid")
    public static final TileEntityType<GridTile> GRID = null;
    @ObjectHolder("crafting_grid")
    public static final TileEntityType<GridTile> CRAFTING_GRID = null;
    @ObjectHolder("pattern_grid")
    public static final TileEntityType<GridTile> PATTERN_GRID = null;
    @ObjectHolder("fluid_grid")
    public static final TileEntityType<GridTile> FLUID_GRID = null;
    @ObjectHolder("importer")
    public static final TileEntityType<ImporterTile> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final TileEntityType<NetworkTransmitterTile> NETWORK_TRANSMITTER = null;
    @ObjectHolder("network_receiver")
    public static final TileEntityType<NetworkReceiverTile> NETWORK_RECEIVER = null;
    @ObjectHolder("relay")
    public static final TileEntityType<RelayTile> RELAY = null;
    @ObjectHolder("cable")
    public static final TileEntityType<CableTile> CABLE = null;
    @ObjectHolder("1k_storage_block")
    public static final TileEntityType<StorageTile> ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder("4k_storage_block")
    public static final TileEntityType<StorageTile> FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("16k_storage_block")
    public static final TileEntityType<StorageTile> SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder("64k_storage_block")
    public static final TileEntityType<StorageTile> SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("creative_storage_block")
    public static final TileEntityType<StorageTile> CREATIVE_STORAGE_BLOCK = null;
    @ObjectHolder("64k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("256k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("1024k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("4096k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("creative_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> CREATIVE_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final TileEntityType<SecurityManagerTile> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final TileEntityType<InterfaceTile> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final TileEntityType<FluidInterfaceTile> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final TileEntityType<WirelessTransmitterTile> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final TileEntityType<StorageMonitorTile> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final TileEntityType<ConstructorTile> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final TileEntityType<DestructorTile> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final TileEntityType<DiskManipulatorTile> DISK_MANIPULATOR = null;
    @ObjectHolder("portable_grid")
    public static final TileEntityType<PortableGridTile> PORTABLE_GRID = null;
    @ObjectHolder("creative_portable_grid")
    public static final TileEntityType<PortableGridTile> CREATIVE_PORTABLE_GRID = null;
    @ObjectHolder("crafter")
    public static final TileEntityType<CrafterTile> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final TileEntityType<CrafterManagerTile> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final TileEntityType<CraftingMonitorTile> CRAFTING_MONITOR = null;

    private RSTiles() {
    }
}
