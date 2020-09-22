package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.*;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class RSBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RS.ID);

    public static final RegistryObject<ImporterBlock> IMPORTER;
    public static final RegistryObject<ExporterBlock> EXPORTER;

    public static final RegistryObject<QuartzEnrichedIronBlock> QUARTZ_ENRICHED_IRON;
    public static final RegistryObject<MachineCasingBlock> MACHINE_CASING;
    public static final RegistryObject<CableBlock> CABLE;
    public static final RegistryObject<DiskDriveBlock> DISK_DRIVE;
    public static final RegistryObject<ExternalStorageBlock> EXTERNAL_STORAGE;
    public static final Map<ItemStorageType, RegistryObject<StorageBlock>> STORAGE_BLOCKS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageBlock>> FLUID_STORAGE_BLOCKS = new HashMap<>();
    public static final RegistryObject<InterfaceBlock> INTERFACE;
    public static final RegistryObject<FluidInterfaceBlock> FLUID_INTERFACE;
    public static final RegistryObject<StorageMonitorBlock> STORAGE_MONITOR;
    public static final RegistryObject<ConstructorBlock> CONSTRUCTOR;
    public static final RegistryObject<DestructorBlock> DESTRUCTOR;
    public static final RegistryObject<PortableGridBlock> PORTABLE_GRID;
    public static final RegistryObject<PortableGridBlock> CREATIVE_PORTABLE_GRID;

    public static final Map<DyeColor, RegistryObject<CrafterBlock>> CRAFTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<RelayBlock>> RELAY = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<NetworkTransmitterBlock>> NETWORK_TRANSMITTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<NetworkReceiverBlock>> NETWORK_RECEIVER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<ControllerBlock>> CONTROLLER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<ControllerBlock>> CREATIVE_CONTROLLER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<GridBlock>> GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<GridBlock>> CRAFTING_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<GridBlock>> PATTERN_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<GridBlock>> FLUID_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<SecurityManagerBlock>> SECURITY_MANAGER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<WirelessTransmitterBlock>> WIRELESS_TRANSMITTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<DiskManipulatorBlock>> DISK_MANIPULATOR = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<CrafterManagerBlock>> CRAFTER_MANAGER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<CraftingMonitorBlock>> CRAFTING_MONITOR = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<DetectorBlock>> DETECTOR = new HashMap<>();

    public static final List<RegistryObject<? extends Block>> COLORED_BLOCKS = new ArrayList<>();

    static {
        QUARTZ_ENRICHED_IRON = BLOCKS.register("quartz_enriched_iron_block", QuartzEnrichedIronBlock::new);
        MACHINE_CASING = BLOCKS.register("machine_casing", MachineCasingBlock::new);
        CABLE = BLOCKS.register("cable", CableBlock::new);
        DISK_DRIVE = BLOCKS.register("disk_drive", DiskDriveBlock::new);
        EXTERNAL_STORAGE = BLOCKS.register("external_storage", ExternalStorageBlock::new);
        IMPORTER = BLOCKS.register("importer", ImporterBlock::new);
        EXPORTER = BLOCKS.register("exporter", ExporterBlock::new);
        INTERFACE = BLOCKS.register("interface", InterfaceBlock::new);
        FLUID_INTERFACE = BLOCKS.register("fluid_interface", FluidInterfaceBlock::new);
        STORAGE_MONITOR = BLOCKS.register("storage_monitor", StorageMonitorBlock::new);
        CONSTRUCTOR = BLOCKS.register("constructor", ConstructorBlock::new);
        DESTRUCTOR = BLOCKS.register("destructor", DestructorBlock::new);
        CREATIVE_PORTABLE_GRID = BLOCKS.register("creative_portable_grid", () -> new PortableGridBlock(PortableGridBlockItem.Type.CREATIVE));
        PORTABLE_GRID = BLOCKS.register("portable_grid", () -> new PortableGridBlock(PortableGridBlockItem.Type.NORMAL));

        for (ItemStorageType type : ItemStorageType.values()) {
            STORAGE_BLOCKS.put(type, BLOCKS.register(type.getName() + "_storage_block", () -> new StorageBlock(type)));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            FLUID_STORAGE_BLOCKS.put(type, BLOCKS.register(type.getName() + "_fluid_storage_block", () -> new FluidStorageBlock(type)));
        }

        registerColoredBlocks(GRID, "grid", () -> new GridBlock(GridType.NORMAL));
        registerColoredBlocks(CRAFTING_GRID, GridType.CRAFTING.getString() + "_grid", () -> new GridBlock(GridType.CRAFTING));
        registerColoredBlocks(PATTERN_GRID, GridType.PATTERN.getString() + "_grid", () -> new GridBlock(GridType.PATTERN));
        registerColoredBlocks(FLUID_GRID, GridType.FLUID.getString() + "_grid", () -> new GridBlock(GridType.FLUID));
        registerColoredBlocks(CONTROLLER, "controller", () -> new ControllerBlock(NetworkType.NORMAL));
        registerColoredBlocks(CREATIVE_CONTROLLER, "creative_controller", () -> new ControllerBlock(NetworkType.CREATIVE));
        registerColoredBlocks(NETWORK_RECEIVER, "network_receiver", NetworkReceiverBlock::new);
        registerColoredBlocks(NETWORK_TRANSMITTER, "network_transmitter", NetworkTransmitterBlock::new);
        registerColoredBlocks(RELAY, "relay", RelayBlock::new);
        registerColoredBlocks(SECURITY_MANAGER, "security_manager", SecurityManagerBlock::new);
        registerColoredBlocks(WIRELESS_TRANSMITTER, "wireless_transmitter", WirelessTransmitterBlock::new);
        registerColoredBlocks(DISK_MANIPULATOR, "disk_manipulator", DiskManipulatorBlock::new);
        registerColoredBlocks(CRAFTER, "crafter", CrafterBlock::new);
        registerColoredBlocks(CRAFTER_MANAGER, "crafter_manager", CrafterManagerBlock::new);
        registerColoredBlocks(CRAFTING_MONITOR, "crafting_monitor", CraftingMonitorBlock::new);
        registerColoredBlocks(DETECTOR, "detector", DetectorBlock::new);
    }

    private static <T extends Block> void registerColoredBlocks(Map<DyeColor, RegistryObject<T>> blockMap, String name, Supplier<T> blockFactory) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color != DyeColor.LIGHT_BLUE ? color + "_" : "";
            RegistryObject<T> block = BLOCKS.register(prefix + name, blockFactory);
            blockMap.put(color, block);
            COLORED_BLOCKS.add(block);
        }
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}