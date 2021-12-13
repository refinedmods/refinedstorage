package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RS.ID)
public final class RSBlockEntities {
    @ObjectHolder("controller")
    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER = null;
    @ObjectHolder("creative_controller")
    public static final BlockEntityType<ControllerBlockEntity> CREATIVE_CONTROLLER = null;
    @ObjectHolder("detector")
    public static final BlockEntityType<DetectorBlockEntity> DETECTOR = null;
    @ObjectHolder("disk_drive")
    public static final BlockEntityType<DiskDriveBlockEntity> DISK_DRIVE = null;
    @ObjectHolder("exporter")
    public static final BlockEntityType<ExporterBlockEntity> EXPORTER = null;
    @ObjectHolder("external_storage")
    public static final BlockEntityType<ExternalStorageBlockEntity> EXTERNAL_STORAGE = null;
    @ObjectHolder("grid")
    public static final BlockEntityType<GridBlockEntity> GRID = null;
    @ObjectHolder("crafting_grid")
    public static final BlockEntityType<GridBlockEntity> CRAFTING_GRID = null;
    @ObjectHolder("pattern_grid")
    public static final BlockEntityType<GridBlockEntity> PATTERN_GRID = null;
    @ObjectHolder("fluid_grid")
    public static final BlockEntityType<GridBlockEntity> FLUID_GRID = null;
    @ObjectHolder("importer")
    public static final BlockEntityType<ImporterBlockEntity> IMPORTER = null;
    @ObjectHolder("network_transmitter")
    public static final BlockEntityType<NetworkTransmitterBlockEntity> NETWORK_TRANSMITTER = null;
    @ObjectHolder("network_receiver")
    public static final BlockEntityType<NetworkReceiverBlockEntity> NETWORK_RECEIVER = null;
    @ObjectHolder("relay")
    public static final BlockEntityType<RelayBlockEntity> RELAY = null;
    @ObjectHolder("cable")
    public static final BlockEntityType<CableBlockEntity> CABLE = null;
    @ObjectHolder("1k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder("4k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("16k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder("64k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder("creative_storage_block")
    public static final BlockEntityType<StorageBlockEntity> CREATIVE_STORAGE_BLOCK = null;
    @ObjectHolder("64k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("256k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("1024k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("4096k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("creative_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> CREATIVE_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder("security_manager")
    public static final BlockEntityType<SecurityManagerBlockEntity> SECURITY_MANAGER = null;
    @ObjectHolder("interface")
    public static final BlockEntityType<InterfaceBlockEntity> INTERFACE = null;
    @ObjectHolder("fluid_interface")
    public static final BlockEntityType<FluidInterfaceBlockEntity> FLUID_INTERFACE = null;
    @ObjectHolder("wireless_transmitter")
    public static final BlockEntityType<WirelessTransmitterBlockEntity> WIRELESS_TRANSMITTER = null;
    @ObjectHolder("storage_monitor")
    public static final BlockEntityType<StorageMonitorBlockEntity> STORAGE_MONITOR = null;
    @ObjectHolder("constructor")
    public static final BlockEntityType<ConstructorBlockEntity> CONSTRUCTOR = null;
    @ObjectHolder("destructor")
    public static final BlockEntityType<DestructorBlockEntity> DESTRUCTOR = null;
    @ObjectHolder("disk_manipulator")
    public static final BlockEntityType<DiskManipulatorBlockEntity> DISK_MANIPULATOR = null;
    @ObjectHolder("portable_grid")
    public static final BlockEntityType<PortableGridBlockEntity> PORTABLE_GRID = null;
    @ObjectHolder("creative_portable_grid")
    public static final BlockEntityType<PortableGridBlockEntity> CREATIVE_PORTABLE_GRID = null;
    @ObjectHolder("crafter")
    public static final BlockEntityType<CrafterBlockEntity> CRAFTER = null;
    @ObjectHolder("crafter_manager")
    public static final BlockEntityType<CrafterManagerBlockEntity> CRAFTER_MANAGER = null;
    @ObjectHolder("crafting_monitor")
    public static final BlockEntityType<CraftingMonitorBlockEntity> CRAFTING_MONITOR = null;

    private RSBlockEntities() {
    }
}
