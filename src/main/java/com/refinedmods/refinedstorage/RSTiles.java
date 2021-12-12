package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.tile.*;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSTiles {
    @ObjectHolder("controller")
    public static final BlockEntityType<ControllerTile> CONTROLLER = null;
    @ObjectHolder("creative_controller")
    public static final BlockEntityType<ControllerTile> CREATIVE_CONTROLLER = null;
    @ObjectHolder("detector")
    public static final BlockEntityType<DetectorTile> DETECTOR = null;
    @ObjectHolder("disk_drive")
    public static final BlockEntityType<DiskDriveTile> DISK_DRIVE = null;
    @ObjectHolder("exporter")
    public static final BlockEntityType<ExporterTile> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final BlockEntityType<ExternalStorageTile> EXTERNAL_STORAGE = null;
    @ObjectHolder("grid")
    public static final BlockEntityType<GridTile> GRID = null;
    @ObjectHolder("crafting_grid")
    public static final BlockEntityType<GridTile> CRAFTING_GRID = null;
    @ObjectHolder("pattern_grid")
    public static final BlockEntityType<GridTile> PATTERN_GRID = null;
    @ObjectHolder("fluid_grid")
    public static final BlockEntityType<GridTile> FLUID_GRID = null;
    @ObjectHolder("importer")
    public static final BlockEntityType<ImporterTile> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final BlockEntityType<NetworkTransmitterTile> NETWORK_TRANSMITTER = null;
    @ObjectHolder("network_receiver")
    public static final BlockEntityType<NetworkReceiverTile> NETWORK_RECEIVER = null;
    @ObjectHolder("relay")
    public static final BlockEntityType<RelayTile> RELAY = null;
    @ObjectHolder("cable")
    public static final BlockEntityType<CableTile> CABLE = null;
    @ObjectHolder("1k_storage_block")
    public static final BlockEntityType<StorageTile> ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder("4k_storage_block")
    public static final BlockEntityType<StorageTile> FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("16k_storage_block")
    public static final BlockEntityType<StorageTile> SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder("64k_storage_block")
    public static final BlockEntityType<StorageTile> SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("creative_storage_block")
    public static final BlockEntityType<StorageTile> CREATIVE_STORAGE_BLOCK = null;
    @ObjectHolder("64k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageTile> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("256k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageTile> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("1024k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageTile> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("4096k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageTile> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("creative_fluid_storage_block")
    public static final BlockEntityType<FluidStorageTile> CREATIVE_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final BlockEntityType<SecurityManagerTile> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final BlockEntityType<InterfaceTile> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final BlockEntityType<FluidInterfaceTile> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final BlockEntityType<WirelessTransmitterTile> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final BlockEntityType<StorageMonitorTile> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final BlockEntityType<ConstructorTile> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final BlockEntityType<DestructorTile> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final BlockEntityType<DiskManipulatorTile> DISK_MANIPULATOR = null;
    @ObjectHolder("portable_grid")
    public static final BlockEntityType<PortableGridTile> PORTABLE_GRID = null;
    @ObjectHolder("creative_portable_grid")
    public static final BlockEntityType<PortableGridTile> CREATIVE_PORTABLE_GRID = null;
    @ObjectHolder("crafter")
    public static final BlockEntityType<CrafterTile> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final BlockEntityType<CrafterManagerTile> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final BlockEntityType<CraftingMonitorTile> CRAFTING_MONITOR = null;

    private RSTiles() {
    }
}
