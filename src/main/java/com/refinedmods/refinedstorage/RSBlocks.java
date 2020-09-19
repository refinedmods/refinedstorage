package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.*;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class RSBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RS.ID);

    public static final RegistryObject<ImporterBlock> IMPORTER;
    public static final RegistryObject<ExporterBlock> EXPORTER;
    public static final RegistryObject<DetectorBlock> DETECTOR;
    public static final RegistryObject<RelayBlock> RELAY;
    public static final RegistryObject<NetworkTransmitterBlock> NETWORK_TRANSMITTER;
    public static final RegistryObject<NetworkReceiverBlock> NETWORK_RECEIVER;
    public static final RegistryObject<QuartzEnrichedIronBlock> QUARTZ_ENRICHED_IRON;
    public static final RegistryObject<MachineCasingBlock> MACHINE_CASING;
    public static final RegistryObject<ControllerBlock> CONTROLLER;
    public static final RegistryObject<ControllerBlock> CREATIVE_CONTROLLER;
    public static final RegistryObject<CableBlock> CABLE;
    public static final RegistryObject<DiskDriveBlock> DISK_DRIVE;
    public static final RegistryObject<ExternalStorageBlock> EXTERNAL_STORAGE;
    public static final RegistryObject<GridBlock> GRID;
    public static final RegistryObject<GridBlock> CRAFTING_GRID;
    public static final RegistryObject<GridBlock> PATTERN_GRID;
    public static final RegistryObject<GridBlock> FLUID_GRID;
    public static final Map<ItemStorageType, RegistryObject<StorageBlock>> STORAGE_BLOCKS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageBlock>> FLUID_STORAGE_BLOCKS = new HashMap<>();
    public static final RegistryObject<SecurityManagerBlock> SECURITY_MANAGER;
    public static final RegistryObject<InterfaceBlock> INTERFACE;
    public static final RegistryObject<FluidInterfaceBlock> FLUID_INTERFACE;
    public static final RegistryObject<WirelessTransmitterBlock> WIRELESS_TRANSMITTER;
    public static final RegistryObject<StorageMonitorBlock> STORAGE_MONITOR;
    public static final RegistryObject<ConstructorBlock> CONSTRUCTOR;
    public static final RegistryObject<DestructorBlock> DESTRUCTOR;
    public static final RegistryObject<DiskManipulatorBlock> DISK_MANIPULATOR;
    public static final RegistryObject<PortableGridBlock> PORTABLE_GRID;
    public static final RegistryObject<PortableGridBlock> CREATIVE_PORTABLE_GRID;
    public static final RegistryObject<CrafterBlock> CRAFTER;
    public static final RegistryObject<CrafterManagerBlock> CRAFTER_MANAGER;
    public static final RegistryObject<CraftingMonitorBlock> CRAFTING_MONITOR;

    static {
        QUARTZ_ENRICHED_IRON = BLOCKS.register("quartz_enriched_iron_block", QuartzEnrichedIronBlock::new);
        CONTROLLER = BLOCKS.register("controller", () -> new ControllerBlock(NetworkType.NORMAL));
        CREATIVE_CONTROLLER = BLOCKS.register("creative_controller", () -> new ControllerBlock(NetworkType.CREATIVE));
        MACHINE_CASING = BLOCKS.register("machine_casing", MachineCasingBlock::new);
        CABLE = BLOCKS.register("cable", CableBlock::new);
        DISK_DRIVE = BLOCKS.register("disk_drive", DiskDriveBlock::new);
        GRID = BLOCKS.register("grid", () -> new GridBlock(GridType.NORMAL));
        CRAFTING_GRID = BLOCKS.register(GridType.CRAFTING.getString() + "_grid", () -> new GridBlock(GridType.CRAFTING));
        PATTERN_GRID = BLOCKS.register(GridType.PATTERN.getString() + "_grid", () -> new GridBlock(GridType.PATTERN));
        FLUID_GRID = BLOCKS.register(GridType.FLUID.getString() + "_grid", () -> new GridBlock(GridType.FLUID));

        for (ItemStorageType type : ItemStorageType.values()) {
            STORAGE_BLOCKS.put(type, BLOCKS.register(type.getName() + "_storage_block", () -> new StorageBlock(type)));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            FLUID_STORAGE_BLOCKS.put(type, BLOCKS.register(type.getName() + "_fluid_storage_block", () -> new FluidStorageBlock(type)));
        }

        EXTERNAL_STORAGE = BLOCKS.register("external_storage", ExternalStorageBlock::new);
        IMPORTER = BLOCKS.register("importer", ImporterBlock::new);
        EXPORTER = BLOCKS.register("exporter", ExporterBlock::new);
        NETWORK_RECEIVER = BLOCKS.register("network_receiver", NetworkReceiverBlock::new);
        NETWORK_TRANSMITTER = BLOCKS.register("network_transmitter", NetworkTransmitterBlock::new);
        RELAY = BLOCKS.register("relay", RelayBlock::new);
        DETECTOR = BLOCKS.register("detector", DetectorBlock::new);
        SECURITY_MANAGER = BLOCKS.register("security_manager", SecurityManagerBlock::new);
        INTERFACE = BLOCKS.register("interface", InterfaceBlock::new);
        FLUID_INTERFACE = BLOCKS.register("fluid_interface", FluidInterfaceBlock::new);
        WIRELESS_TRANSMITTER = BLOCKS.register("wireless_transmitter", WirelessTransmitterBlock::new);
        STORAGE_MONITOR = BLOCKS.register("storage_monitor", StorageMonitorBlock::new);
        CONSTRUCTOR = BLOCKS.register("constructor", ConstructorBlock::new);
        DESTRUCTOR = BLOCKS.register("destructor", DestructorBlock::new);
        DISK_MANIPULATOR = BLOCKS.register("disk_manipulator", DiskManipulatorBlock::new);
        CREATIVE_PORTABLE_GRID = BLOCKS.register("creative_portable_grid", () -> new PortableGridBlock(PortableGridBlockItem.Type.CREATIVE));
        PORTABLE_GRID = BLOCKS.register("portable_grid", () -> new PortableGridBlock(PortableGridBlockItem.Type.NORMAL));
        CRAFTER = BLOCKS.register("crafter", CrafterBlock::new);
        CRAFTER_MANAGER = BLOCKS.register("crafter_manager", CrafterManagerBlock::new);
        CRAFTING_MONITOR = BLOCKS.register("crafting_monitor", CraftingMonitorBlock::new);
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}