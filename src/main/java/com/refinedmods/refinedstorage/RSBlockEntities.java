package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public final class RSBlockEntities {
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:controller")
    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:creative_controller")
    public static final BlockEntityType<ControllerBlockEntity> CREATIVE_CONTROLLER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:detector")
    public static final BlockEntityType<DetectorBlockEntity> DETECTOR = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:disk_drive")
    public static final BlockEntityType<DiskDriveBlockEntity> DISK_DRIVE = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:exporter")
    public static final BlockEntityType<ExporterBlockEntity> EXPORTER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:external_storage")
    public static final BlockEntityType<ExternalStorageBlockEntity> EXTERNAL_STORAGE = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:grid")
    public static final BlockEntityType<GridBlockEntity> GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:crafting_grid")
    public static final BlockEntityType<GridBlockEntity> CRAFTING_GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:pattern_grid")
    public static final BlockEntityType<GridBlockEntity> PATTERN_GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:fluid_grid")
    public static final BlockEntityType<GridBlockEntity> FLUID_GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:importer")
    public static final BlockEntityType<ImporterBlockEntity> IMPORTER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:network_transmitter")
    public static final BlockEntityType<NetworkTransmitterBlockEntity> NETWORK_TRANSMITTER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:network_receiver")
    public static final BlockEntityType<NetworkReceiverBlockEntity> NETWORK_RECEIVER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:relay")
    public static final BlockEntityType<RelayBlockEntity> RELAY = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:cable")
    public static final BlockEntityType<CableBlockEntity> CABLE = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:1k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> ONE_K_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:4k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:16k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> SIXTEEN_K_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:64k_storage_block")
    public static final BlockEntityType<StorageBlockEntity> SIXTY_FOUR_K_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:creative_storage_block")
    public static final BlockEntityType<StorageBlockEntity> CREATIVE_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:64k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:256k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:1024k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:4096k_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:creative_fluid_storage_block")
    public static final BlockEntityType<FluidStorageBlockEntity> CREATIVE_FLUID_STORAGE_BLOCK = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:security_manager")
    public static final BlockEntityType<SecurityManagerBlockEntity> SECURITY_MANAGER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:interface")
    public static final BlockEntityType<InterfaceBlockEntity> INTERFACE = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:fluid_interface")
    public static final BlockEntityType<FluidInterfaceBlockEntity> FLUID_INTERFACE = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:wireless_transmitter")
    public static final BlockEntityType<WirelessTransmitterBlockEntity> WIRELESS_TRANSMITTER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:storage_monitor")
    public static final BlockEntityType<StorageMonitorBlockEntity> STORAGE_MONITOR = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:constructor")
    public static final BlockEntityType<ConstructorBlockEntity> CONSTRUCTOR = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:destructor")
    public static final BlockEntityType<DestructorBlockEntity> DESTRUCTOR = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:disk_manipulator")
    public static final BlockEntityType<DiskManipulatorBlockEntity> DISK_MANIPULATOR = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:portable_grid")
    public static final BlockEntityType<PortableGridBlockEntity> PORTABLE_GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:creative_portable_grid")
    public static final BlockEntityType<PortableGridBlockEntity> CREATIVE_PORTABLE_GRID = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:crafter")
    public static final BlockEntityType<CrafterBlockEntity> CRAFTER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:crafter_manager")
    public static final BlockEntityType<CrafterManagerBlockEntity> CRAFTER_MANAGER = null;
    @ObjectHolder(registryName = "minecraft:block_entity_type", value = "refinedstorage:crafting_monitor")
    public static final BlockEntityType<CraftingMonitorBlockEntity> CRAFTING_MONITOR = null;

    private RSBlockEntities() {
    }
}
