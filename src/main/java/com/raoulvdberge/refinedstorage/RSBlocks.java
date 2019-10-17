package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.block.*;
import net.minecraftforge.registries.ObjectHolder;

public final class RSBlocks {
    @ObjectHolder(RS.ID + ":importer")
    public static final ImporterBlock IMPORTER = null;

    public static final BlockExporter EXPORTER = new BlockExporter();
    public static final BlockDetector DETECTOR = new BlockDetector();
    public static final BlockDestructor DESTRUCTOR = new BlockDestructor();
    public static final BlockConstructor CONSTRUCTOR = new BlockConstructor();
    public static final BlockRelay RELAY = new BlockRelay();
    public static final BlockInterface INTERFACE = new BlockInterface();
    public static final BlockCraftingMonitor CRAFTING_MONITOR = new BlockCraftingMonitor();
    public static final BlockWirelessTransmitter WIRELESS_TRANSMITTER = new BlockWirelessTransmitter();
    public static final BlockCrafter CRAFTER = new BlockCrafter();
    public static final BlockNetworkTransmitter NETWORK_TRANSMITTER = new BlockNetworkTransmitter();
    public static final BlockNetworkReceiver NETWORK_RECEIVER = new BlockNetworkReceiver();
    public static final BlockFluidInterface FLUID_INTERFACE = new BlockFluidInterface();
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

    @ObjectHolder(RS.ID + ":grid")
    public static final GridBlock GRID = null;
    @ObjectHolder(RS.ID + ":crafting_grid")
    public static final GridBlock CRAFTING_GRID = null;
    @ObjectHolder(RS.ID + ":pattern_grid")
    public static final GridBlock PATTERN_GRID = null;
    @ObjectHolder(RS.ID + ":fluid_grid")
    public static final GridBlock FLUID_GRID = null;

    @ObjectHolder(RS.ID + ":1k_storage_block")
    public static final StorageBlock ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":4k_storage_block")
    public static final StorageBlock FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":16k_storage_block")
    public static final StorageBlock SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":64k_storage_block")
    public static final StorageBlock SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":creative_storage_block")
    public static final StorageBlock CREATIVE_STORAGE_BLOCK = null;

    @ObjectHolder(RS.ID + ":64k_fluid_storage_block")
    public static final FluidStorageBlock SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":256k_fluid_storage_block")
    public static final FluidStorageBlock TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":1024k_fluid_storage_block")
    public static final FluidStorageBlock THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":4096k_fluid_storage_block")
    public static final FluidStorageBlock FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(RS.ID + ":creative_fluid_storage_block")
    public static final FluidStorageBlock CREATIVE_FLUID_STORAGE_BLOCK = null;

    @ObjectHolder(RS.ID + ":external_storage")
    public static final ExternalStorageBlock EXTERNAL_STORAGE = null;

    public static final BlockStorageMonitor STORAGE_MONITOR = new BlockStorageMonitor();
    public static final BlockPortableGrid PORTABLE_GRID = new BlockPortableGrid();
    public static final BlockCrafterManager CRAFTER_MANAGER = new BlockCrafterManager();
}