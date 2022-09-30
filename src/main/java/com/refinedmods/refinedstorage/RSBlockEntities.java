package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.blockentity.*;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RS.ID);

    public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CONTROLLER =
        REGISTRY.register("controller", () -> registerSynchronizationParameters(ControllerBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new ControllerBlockEntity(NetworkType.NORMAL, pos, state), RSBlocks.CONTROLLER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CREATIVE_CONTROLLER =
        REGISTRY.register("creative_controller", () -> registerSynchronizationParameters(ControllerBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new ControllerBlockEntity(NetworkType.CREATIVE, pos, state), RSBlocks.CREATIVE_CONTROLLER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<DetectorBlockEntity>> DETECTOR =
        REGISTRY.register("detector", () -> registerSynchronizationParameters(DetectorBlockEntity.SPEC, BlockEntityType.Builder.of(DetectorBlockEntity::new, RSBlocks.DETECTOR.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<DiskDriveBlockEntity>> DISK_DRIVE =
        REGISTRY.register("disk_drive", () -> registerSynchronizationParameters(DiskDriveBlockEntity.SPEC, BlockEntityType.Builder.of(DiskDriveBlockEntity::new, RSBlocks.DISK_DRIVE.get()).build(null)));
    public static final RegistryObject<BlockEntityType<ExporterBlockEntity>> EXPORTER =
        REGISTRY.register("exporter", () -> registerSynchronizationParameters(ExporterBlockEntity.SPEC, BlockEntityType.Builder.of(ExporterBlockEntity::new, RSBlocks.EXPORTER.get()).build(null)));
    public static final RegistryObject<BlockEntityType<ExternalStorageBlockEntity>> EXTERNAL_STORAGE =
        REGISTRY.register("external_storage", () -> registerSynchronizationParameters(ExternalStorageBlockEntity.SPEC, BlockEntityType.Builder.of(ExternalStorageBlockEntity::new, RSBlocks.EXTERNAL_STORAGE.get()).build(null)));
    public static final RegistryObject<BlockEntityType<GridBlockEntity>> GRID =
        REGISTRY.register("grid", () -> registerSynchronizationParameters(GridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.NORMAL, pos, state), RSBlocks.GRID.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<GridBlockEntity>> CRAFTING_GRID =
        REGISTRY.register("crafting_grid", () -> registerSynchronizationParameters(GridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.CRAFTING, pos, state), RSBlocks.CRAFTING_GRID.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<GridBlockEntity>> PATTERN_GRID =
        REGISTRY.register("pattern_grid", () -> registerSynchronizationParameters(GridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.PATTERN, pos, state), RSBlocks.PATTERN_GRID.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<GridBlockEntity>> FLUID_GRID =
        REGISTRY.register("fluid_grid", () -> registerSynchronizationParameters(GridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new GridBlockEntity(GridType.FLUID, pos, state), RSBlocks.FLUID_GRID.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<ImporterBlockEntity>> IMPORTER =
        REGISTRY.register("importer", () -> registerSynchronizationParameters(ImporterBlockEntity.SPEC, BlockEntityType.Builder.of(ImporterBlockEntity::new, RSBlocks.IMPORTER.get()).build(null)));
    public static final RegistryObject<BlockEntityType<NetworkTransmitterBlockEntity>> NETWORK_TRANSMITTER =
        REGISTRY.register("network_transmitter", () -> registerSynchronizationParameters(NetworkTransmitterBlockEntity.SPEC, BlockEntityType.Builder.of(NetworkTransmitterBlockEntity::new, RSBlocks.NETWORK_TRANSMITTER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<NetworkReceiverBlockEntity>> NETWORK_RECEIVER =
        REGISTRY.register("network_receiver", () -> registerSynchronizationParameters(NetworkReceiverBlockEntity.SPEC, BlockEntityType.Builder.of(NetworkReceiverBlockEntity::new, RSBlocks.NETWORK_RECEIVER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<RelayBlockEntity>> RELAY =
        REGISTRY.register("relay", () -> registerSynchronizationParameters(RelayBlockEntity.SPEC, BlockEntityType.Builder.of(RelayBlockEntity::new, RSBlocks.RELAY.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE =
        REGISTRY.register("cable", () -> registerSynchronizationParameters(CableBlockEntity.SPEC, BlockEntityType.Builder.of(CableBlockEntity::new, RSBlocks.CABLE.get()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> ONE_K_STORAGE_BLOCK =
        REGISTRY.register("1k_storage_block", () -> registerSynchronizationParameters(StorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.ONE_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.ONE_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> FOUR_K_STORAGE_BLOCK =
        REGISTRY.register("4k_storage_block", () -> registerSynchronizationParameters(StorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.FOUR_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.FOUR_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> SIXTEEN_K_STORAGE_BLOCK =
        REGISTRY.register("16k_storage_block", () -> registerSynchronizationParameters(StorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.SIXTEEN_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTEEN_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> SIXTY_FOUR_K_STORAGE_BLOCK =
        REGISTRY.register("64k_storage_block", () -> registerSynchronizationParameters(StorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.SIXTY_FOUR_K, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.SIXTY_FOUR_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> CREATIVE_STORAGE_BLOCK =
        REGISTRY.register("creative_storage_block", () -> registerSynchronizationParameters(StorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(ItemStorageType.CREATIVE, pos, state), RSBlocks.STORAGE_BLOCKS.get(ItemStorageType.CREATIVE).get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidStorageBlockEntity>> SIXTY_FOUR_K_FLUID_STORAGE_BLOCK =
        REGISTRY.register("64k_fluid_storage_block", () -> registerSynchronizationParameters(FluidStorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.SIXTY_FOUR_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.SIXTY_FOUR_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidStorageBlockEntity>> TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK =
        REGISTRY.register("256k_fluid_storage_block", () -> registerSynchronizationParameters(FluidStorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidStorageBlockEntity>> THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK =
        REGISTRY.register("1024k_fluid_storage_block", () -> registerSynchronizationParameters(FluidStorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.THOUSAND_TWENTY_FOUR_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.THOUSAND_TWENTY_FOUR_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidStorageBlockEntity>> FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK =
        REGISTRY.register("4096k_fluid_storage_block", () -> registerSynchronizationParameters(FluidStorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K).get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidStorageBlockEntity>> CREATIVE_FLUID_STORAGE_BLOCK =
        REGISTRY.register("creative_fluid_storage_block", () -> registerSynchronizationParameters(FluidStorageBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new FluidStorageBlockEntity(FluidStorageType.CREATIVE, pos, state), RSBlocks.FLUID_STORAGE_BLOCKS.get(FluidStorageType.CREATIVE).get()).build(null)));
    public static final RegistryObject<BlockEntityType<SecurityManagerBlockEntity>> SECURITY_MANAGER =
        REGISTRY.register("security_manager", () -> registerSynchronizationParameters(SecurityManagerBlockEntity.SPEC, BlockEntityType.Builder.of(SecurityManagerBlockEntity::new, RSBlocks.SECURITY_MANAGER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<InterfaceBlockEntity>> INTERFACE =
        REGISTRY.register("interface", () -> registerSynchronizationParameters(InterfaceBlockEntity.SPEC, BlockEntityType.Builder.of(InterfaceBlockEntity::new, RSBlocks.INTERFACE.get()).build(null)));
    public static final RegistryObject<BlockEntityType<FluidInterfaceBlockEntity>> FLUID_INTERFACE =
        REGISTRY.register("fluid_interface", () -> registerSynchronizationParameters(FluidInterfaceBlockEntity.SPEC, BlockEntityType.Builder.of(FluidInterfaceBlockEntity::new, RSBlocks.FLUID_INTERFACE.get()).build(null)));
    public static final RegistryObject<BlockEntityType<WirelessTransmitterBlockEntity>> WIRELESS_TRANSMITTER =
        REGISTRY.register("wireless_transmitter", () -> registerSynchronizationParameters(WirelessTransmitterBlockEntity.SPEC, BlockEntityType.Builder.of(WirelessTransmitterBlockEntity::new, RSBlocks.WIRELESS_TRANSMITTER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<StorageMonitorBlockEntity>> STORAGE_MONITOR =
        REGISTRY.register("storage_monitor", () -> registerSynchronizationParameters(StorageMonitorBlockEntity.SPEC, BlockEntityType.Builder.of(StorageMonitorBlockEntity::new, RSBlocks.STORAGE_MONITOR.get()).build(null)));
    public static final RegistryObject<BlockEntityType<ConstructorBlockEntity>> CONSTRUCTOR =
        REGISTRY.register("constructor", () -> registerSynchronizationParameters(ConstructorBlockEntity.SPEC, BlockEntityType.Builder.of(ConstructorBlockEntity::new, RSBlocks.CONSTRUCTOR.get()).build(null)));
    public static final RegistryObject<BlockEntityType<DestructorBlockEntity>> DESTRUCTOR =
        REGISTRY.register("destructor", () -> registerSynchronizationParameters(DestructorBlockEntity.SPEC, BlockEntityType.Builder.of(DestructorBlockEntity::new, RSBlocks.DESTRUCTOR.get()).build(null)));
    public static final RegistryObject<BlockEntityType<DiskManipulatorBlockEntity>> DISK_MANIPULATOR =
        REGISTRY.register("disk_manipulator", () -> registerSynchronizationParameters(DiskManipulatorBlockEntity.SPEC, BlockEntityType.Builder.of(DiskManipulatorBlockEntity::new, RSBlocks.DISK_MANIPULATOR.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<PortableGridBlockEntity>> PORTABLE_GRID =
        REGISTRY.register("portable_grid", () -> registerSynchronizationParameters(PortableGridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new PortableGridBlockEntity(PortableGridBlockItem.Type.NORMAL, pos, state), RSBlocks.PORTABLE_GRID.get()).build(null)));
    public static final RegistryObject<BlockEntityType<PortableGridBlockEntity>> CREATIVE_PORTABLE_GRID =
        REGISTRY.register("creative_portable_grid", () -> registerSynchronizationParameters(PortableGridBlockEntity.SPEC, BlockEntityType.Builder.of((pos, state) -> new PortableGridBlockEntity(PortableGridBlockItem.Type.CREATIVE, pos, state), RSBlocks.CREATIVE_PORTABLE_GRID.get()).build(null)));
    public static final RegistryObject<BlockEntityType<CrafterBlockEntity>> CRAFTER =
        REGISTRY.register("crafter", () -> registerSynchronizationParameters(CrafterBlockEntity.SPEC, BlockEntityType.Builder.of(CrafterBlockEntity::new, RSBlocks.CRAFTER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<CrafterManagerBlockEntity>> CRAFTER_MANAGER =
        REGISTRY.register("crafter_manager", () -> registerSynchronizationParameters(CrafterManagerBlockEntity.SPEC, BlockEntityType.Builder.of(CrafterManagerBlockEntity::new, RSBlocks.CRAFTER_MANAGER.getBlocks()).build(null)));
    public static final RegistryObject<BlockEntityType<CraftingMonitorBlockEntity>> CRAFTING_MONITOR =
        REGISTRY.register("crafting_monitor", () -> registerSynchronizationParameters(CraftingMonitorBlockEntity.SPEC, BlockEntityType.Builder.of(CraftingMonitorBlockEntity::new, RSBlocks.CRAFTING_MONITOR.getBlocks()).build(null)));

    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntitySynchronizationSpec spec, BlockEntityType<T> t) {
        spec.getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
        return t;
    }

    private RSBlockEntities() {
    }
}
