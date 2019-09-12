package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RSTiles {
    @ObjectHolder(RS.ID + ":constructor")
    public static final TileEntityType<TileConstructor> CONSTRUCTOR = null;
    @ObjectHolder(RS.ID + ":controller")
    public static final TileEntityType<TileController> CONTROLLER = null;
    @ObjectHolder(RS.ID + ":crafter")
    public static final TileEntityType<TileCrafter> CRAFTER = null;
    @ObjectHolder(RS.ID + ":crafter_manager")
    public static final TileEntityType<TileCrafter> CRAFTER_MANAGER = null;
    @ObjectHolder(RS.ID + ":crafting_monitor")
    public static final TileEntityType<TileCraftingMonitor> CRAFTING_MONITOR = null;
    @ObjectHolder(RS.ID + ":destructor")
    public static final TileEntityType<TileDestructor> DESTRUCTOR = null;
    @ObjectHolder(RS.ID + ":detector")
    public static final TileEntityType<TileDetector> DETECTOR = null;
    @ObjectHolder(RS.ID + ":disk_drive")
    public static final TileEntityType<TileDiskDrive> DISK_DRIVE = null;
    @ObjectHolder(RS.ID + ":disk_manipulator")
    public static final TileEntityType<TileDiskManipulator> DISK_MANIPULATOR = null;
    @ObjectHolder(RS.ID + ":exporter")
    public static final TileEntityType<TileExporter> EXPORTER = null;
    @ObjectHolder(RS.ID + ":external_storage")
    public static final TileEntityType<TileExternalStorage> EXTERNAL_STORAGE = null;
    @ObjectHolder(RS.ID + ":fluid_interface")
    public static final TileEntityType<TileFluidInterface> FLUID_INTERFACE = null;
    @ObjectHolder(RS.ID + ":fluid_storage")
    public static final TileEntityType<TileFluidInterface> FLUID_STORAGE = null;
    @ObjectHolder(RS.ID + ":grid")
    public static final TileEntityType<TileGrid> GRID = null;
    @ObjectHolder(RS.ID + ":importer")
    public static final TileEntityType<TileImporter> IMPORTER = null;
    @ObjectHolder(RS.ID + ":interface")
    public static final TileEntityType<TileImporter> INTERFACE = null;
    @ObjectHolder(RS.ID + ":network_transmitter")
    public static final TileEntityType<TileNetworkTransmitter> NETWORK_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":network_receiver")
    public static final TileEntityType<TileNetworkReceiver> NETWORK_RECEIVER = null;
    @ObjectHolder(RS.ID + ":reader")
    public static final TileEntityType<TileReader> READER = null;
    @ObjectHolder(RS.ID + ":writer")
    public static final TileEntityType<TileWriter> WRITER = null;
    @ObjectHolder(RS.ID + ":relay")
    public static final TileEntityType<TileRelay> RELAY = null;
    @ObjectHolder(RS.ID + ":security_manager")
    public static final TileEntityType<TileSecurityManager> SECURITY_MANAGER = null;
    @ObjectHolder(RS.ID + ":storage")
    public static final TileEntityType<TileStorage> STORAGE = null;
    @ObjectHolder(RS.ID + ":storage_monitor")
    public static final TileEntityType<TileStorageMonitor> STORAGE_MONITOR = null;
    @ObjectHolder(RS.ID + ":wireless_transmitter")
    public static final TileEntityType<TileSecurityManager> WIRELESS_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":cable")
    public static final TileEntityType<TileCable> CABLE = null;
    @ObjectHolder(RS.ID + ":portable_grid")
    public static final TileEntityType<TilePortableGrid> PORTABLE_GRID = null;
}
