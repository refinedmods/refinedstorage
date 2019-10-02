package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.block.*;
import net.minecraftforge.registries.ObjectHolder;

public final class RSBlocks {
    public static final BlockGrid GRID = new BlockGrid();
    public static final BlockExternalStorage EXTERNAL_STORAGE = new BlockExternalStorage();
    public static final BlockImporter IMPORTER = new BlockImporter();
    public static final BlockExporter EXPORTER = new BlockExporter();
    public static final BlockDetector DETECTOR = new BlockDetector();
    public static final BlockDestructor DESTRUCTOR = new BlockDestructor();
    public static final BlockConstructor CONSTRUCTOR = new BlockConstructor();
    public static final BlockStorage STORAGE = new BlockStorage();
    public static final BlockRelay RELAY = new BlockRelay();
    public static final BlockInterface INTERFACE = new BlockInterface();
    public static final BlockCraftingMonitor CRAFTING_MONITOR = new BlockCraftingMonitor();
    public static final BlockWirelessTransmitter WIRELESS_TRANSMITTER = new BlockWirelessTransmitter();
    public static final BlockCrafter CRAFTER = new BlockCrafter();
    public static final BlockNetworkTransmitter NETWORK_TRANSMITTER = new BlockNetworkTransmitter();
    public static final BlockNetworkReceiver NETWORK_RECEIVER = new BlockNetworkReceiver();
    public static final BlockFluidInterface FLUID_INTERFACE = new BlockFluidInterface();
    public static final BlockFluidStorage FLUID_STORAGE = new BlockFluidStorage();
    public static final BlockDiskManipulator DISK_MANIPULATOR = new BlockDiskManipulator();
    public static final BlockReader READER = new BlockReader();
    public static final BlockWriter WRITER = new BlockWriter();
    public static final BlockSecurityManager SECURITY_MANAGER = new BlockSecurityManager();

    @ObjectHolder(RS.ID + ":quartz_enriched_iron_block")
    public static final QuartzEnrichedIronBlock QUARTZ_ENRICHED_IRON = null;

    @ObjectHolder(RS.ID + ":machine_casing")
    public static final MachineCasingBlock MACHINE_CASING = null;

    @ObjectHolder(RS.ID + ":controller")
    public static final ControllerBlock CONTROLLER = null;
    @ObjectHolder(RS.ID + ":creative_controller")
    public static final ControllerBlock CREATIVE_CONTROLLER = null;

    @ObjectHolder(RS.ID + ":cable")
    public static final CableBlock CABLE = null;

    @ObjectHolder(RS.ID + ":disk_drive")
    public static final DiskDriveBlock DISK_DRIVE = null;

    public static final BlockStorageMonitor STORAGE_MONITOR = new BlockStorageMonitor();
    public static final BlockPortableGrid PORTABLE_GRID = new BlockPortableGrid();
    public static final BlockCrafterManager CRAFTER_MANAGER = new BlockCrafterManager();
}