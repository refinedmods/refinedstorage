package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.block.*;
import net.minecraftforge.registries.ObjectHolder;

public final class RSBlocks {
    @ObjectHolder(RS.ID + ":importer")
    public static final ImporterBlock IMPORTER = null;
    @ObjectHolder(RS.ID + ":exporter")
    public static final ExporterBlock EXPORTER = null;
    @ObjectHolder(RS.ID + ":detector")
    public static final DetectorBlock DETECTOR = null;
    @ObjectHolder(RS.ID + ":relay")
    public static final RelayBlock RELAY = null;
    @ObjectHolder(RS.ID + ":network_transmitter")
    public static final NetworkTransmitterBlock NETWORK_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":network_receiver")
    public static final NetworkReceiverBlock NETWORK_RECEIVER = null;
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
    @ObjectHolder(RS.ID + ":external_storage")
    public static final ExternalStorageBlock EXTERNAL_STORAGE = null;
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
    @ObjectHolder(RS.ID + ":security_manager")
    public static final SecurityManagerBlock SECURITY_MANAGER = null;
    @ObjectHolder(RS.ID + ":interface")
    public static final InterfaceBlock INTERFACE = null;
    @ObjectHolder(RS.ID + ":fluid_interface")
    public static final FluidInterfaceBlock FLUID_INTERFACE = null;
    @ObjectHolder(RS.ID + ":wireless_transmitter")
    public static final WirelessTransmitterBlock WIRELESS_TRANSMITTER = null;
    @ObjectHolder(RS.ID + ":storage_monitor")
    public static final StorageMonitorBlock STORAGE_MONITOR = null;
    @ObjectHolder(RS.ID + ":constructor")
    public static final ConstructorBlock CONSTRUCTOR = null;
    @ObjectHolder(RS.ID + ":destructor")
    public static final DestructorBlock DESTRUCTOR = null;
    @ObjectHolder(RS.ID + ":disk_manipulator")
    public static final DiskManipulatorBlock DISK_MANIPULATOR = null;

    public static final BlockCraftingMonitor CRAFTING_MONITOR = new BlockCraftingMonitor();
    public static final BlockCrafter CRAFTER = new BlockCrafter();
    public static final BlockPortableGrid PORTABLE_GRID = new BlockPortableGrid();
    public static final BlockCrafterManager CRAFTER_MANAGER = new BlockCrafterManager();
}