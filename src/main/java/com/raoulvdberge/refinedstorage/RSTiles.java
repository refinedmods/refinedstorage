package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RSTiles {
    @ObjectHolder(RS.ID + ":controller")
    public static final TileEntityType<ControllerTile> CONTROLLER = null;
    @ObjectHolder(RS.ID + ":creative_controller")
    public static final TileEntityType<ControllerTile> CREATIVE_CONTROLLER = null;
    @ObjectHolder(RS.ID + ":detector")
    public static final TileEntityType<DetectorTile> DETECTOR = null;
    @ObjectHolder(RS.ID + ":disk_drive")
    public static final TileEntityType<DiskDriveTile> DISK_DRIVE = null;
    @ObjectHolder(RS.ID + ":exporter")
    public static final TileEntityType<ExporterTile> EXPORTER = null;
    @ObjectHolder(RS.ID + ":external_storage")
    public static final TileEntityType<ExternalStorageTile> EXTERNAL_STORAGE = null;
    @ObjectHolder(RS.ID + ":grid")
    public static final TileEntityType<GridTile> GRID = null;
    @ObjectHolder(RS.ID + ":crafting_grid")
    public static final TileEntityType<GridTile> CRAFTING_GRID = null;
    @ObjectHolder(RS.ID + ":pattern_grid")
    public static final TileEntityType<GridTile> PATTERN_GRID = null;
    @ObjectHolder(RS.ID + ":fluid_grid")
    public static final TileEntityType<GridTile> FLUID_GRID = null;
    @ObjectHolder(RS.ID + ":importer")
    public static final TileEntityType<ImporterTile> IMPORTER = null;
    @ObjectHolder(RS.ID + ":network_transmitter")
    public static final TileEntityType<NetworkTransmitterTile> NETWORK_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":network_receiver")
    public static final TileEntityType<NetworkReceiverTile> NETWORK_RECEIVER = null;
    @ObjectHolder(RS.ID + ":relay")
    public static final TileEntityType<RelayTile> RELAY = null;
    @ObjectHolder(RS.ID + ":cable")
    public static final TileEntityType<CableTile> CABLE = null;
    @ObjectHolder(RS.ID + ":1k_storage_block")
    public static final TileEntityType<StorageTile> ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":4k_storage_block")
    public static final TileEntityType<StorageTile> FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":16k_storage_block")
    public static final TileEntityType<StorageTile> SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":64k_storage_block")
    public static final TileEntityType<StorageTile> SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":creative_storage_block")
    public static final TileEntityType<StorageTile> CREATIVE_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":64k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":256k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":1024k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":4096k_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":creative_fluid_storage_block")
    public static final TileEntityType<FluidStorageTile> CREATIVE_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":security_manager")
    public static final TileEntityType<SecurityManagerTile> SECURITY_MANAGER = null;
    @ObjectHolder(RS.ID + ":interface")
    public static final TileEntityType<InterfaceTile> INTERFACE = null;
    @ObjectHolder(RS.ID + ":fluid_interface")
    public static final TileEntityType<FluidInterfaceTile> FLUID_INTERFACE = null;
    @ObjectHolder(RS.ID + ":wireless_transmitter")
    public static final TileEntityType<WirelessTransmitterTile> WIRELESS_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":storage_monitor")
    public static final TileEntityType<StorageMonitorTile> STORAGE_MONITOR = null;
    @ObjectHolder(RS.ID + ":constructor")
    public static final TileEntityType<ConstructorTile> CONSTRUCTOR = null;
    @ObjectHolder(RS.ID + ":destructor")
    public static final TileEntityType<DestructorTile> DESTRUCTOR = null;
    @ObjectHolder(RS.ID + ":disk_manipulator")
    public static final TileEntityType<DiskManipulatorTile> DISK_MANIPULATOR = null;
    @ObjectHolder(RS.ID + ":portable_grid")
    public static final TileEntityType<PortableGridTile> PORTABLE_GRID = null;
    @ObjectHolder(RS.ID + ":creative_portable_grid")
    public static final TileEntityType<PortableGridTile> CREATIVE_PORTABLE_GRID = null;
    
    //@ObjectHolder(RS.ID + ":crafter")
    public static final TileEntityType<TileCrafter> CRAFTER = null;
    //@ObjectHolder(RS.ID + ":crafter_manager")
    public static final TileEntityType<TileCrafter> CRAFTER_MANAGER = null;
    //@ObjectHolder(RS.ID + ":crafting_monitor")
    public static final TileEntityType<TileCraftingMonitor> CRAFTING_MONITOR = null;
}
