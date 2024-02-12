package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.*;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.util.BlockColorMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RSBlocks {
    public static final DeferredHolder<Block, ImporterBlock> IMPORTER;
    public static final DeferredHolder<Block, ExporterBlock> EXPORTER;
    public static final DeferredHolder<Block, QuartzEnrichedIronBlock> QUARTZ_ENRICHED_IRON;
    public static final DeferredHolder<Block, MachineCasingBlock> MACHINE_CASING;
    public static final DeferredHolder<Block, CableBlock> CABLE;
    public static final DeferredHolder<Block, DiskDriveBlock> DISK_DRIVE;
    public static final DeferredHolder<Block, ExternalStorageBlock> EXTERNAL_STORAGE;
    public static final Map<ItemStorageType, DeferredHolder<Block, StorageBlock>> STORAGE_BLOCKS = new EnumMap<>(ItemStorageType.class);
    public static final Map<FluidStorageType, DeferredHolder<Block, FluidStorageBlock>> FLUID_STORAGE_BLOCKS = new EnumMap<>(FluidStorageType.class);
    public static final Map<TagKey<Block>, BlockColorMap<?>> COLORED_BLOCK_TAGS = new HashMap<>();
    public static final DeferredHolder<Block, InterfaceBlock> INTERFACE;
    public static final DeferredHolder<Block, FluidInterfaceBlock> FLUID_INTERFACE;
    public static final DeferredHolder<Block, StorageMonitorBlock> STORAGE_MONITOR;
    public static final DeferredHolder<Block, ConstructorBlock> CONSTRUCTOR;
    public static final DeferredHolder<Block, DestructorBlock> DESTRUCTOR;
    public static final DeferredHolder<Block, PortableGridBlock> PORTABLE_GRID;
    public static final DeferredHolder<Block, PortableGridBlock> CREATIVE_PORTABLE_GRID;
    public static final List<DeferredHolder<Block, ? extends Block>> COLORED_BLOCKS = new ArrayList<>();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, RS.ID);
    public static final BlockColorMap<CrafterBlock> CRAFTER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<RelayBlock> RELAY = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<NetworkTransmitterBlock> NETWORK_TRANSMITTER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<NetworkReceiverBlock> NETWORK_RECEIVER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<ControllerBlock> CONTROLLER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<ControllerBlock> CREATIVE_CONTROLLER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<GridBlock> GRID = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<GridBlock> CRAFTING_GRID = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<GridBlock> PATTERN_GRID = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<GridBlock> FLUID_GRID = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<SecurityManagerBlock> SECURITY_MANAGER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<WirelessTransmitterBlock> WIRELESS_TRANSMITTER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<DiskManipulatorBlock> DISK_MANIPULATOR = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<CrafterManagerBlock> CRAFTER_MANAGER = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<CraftingMonitorBlock> CRAFTING_MONITOR = new BlockColorMap<>(BLOCKS);
    public static final BlockColorMap<DetectorBlock> DETECTOR = new BlockColorMap<>(BLOCKS);
    private static final String GRID_SUFFIX = "_grid";

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

        GRID.registerBlocks("grid", () -> new GridBlock(GridType.NORMAL));
        CRAFTING_GRID.registerBlocks(GridType.CRAFTING.getSerializedName() + GRID_SUFFIX, () -> new GridBlock(GridType.CRAFTING));
        PATTERN_GRID.registerBlocks(GridType.PATTERN.getSerializedName() + GRID_SUFFIX, () -> new GridBlock(GridType.PATTERN));
        FLUID_GRID.registerBlocks(GridType.FLUID.getSerializedName() + GRID_SUFFIX, () -> new GridBlock(GridType.FLUID));
        CONTROLLER.registerBlocks("controller", () -> new ControllerBlock(NetworkType.NORMAL));
        CREATIVE_CONTROLLER.registerBlocks("creative_controller", () -> new ControllerBlock(NetworkType.CREATIVE));
        NETWORK_RECEIVER.registerBlocks("network_receiver", NetworkReceiverBlock::new);
        NETWORK_TRANSMITTER.registerBlocks("network_transmitter", NetworkTransmitterBlock::new);
        RELAY.registerBlocks("relay", RelayBlock::new);
        SECURITY_MANAGER.registerBlocks("security_manager", SecurityManagerBlock::new);
        WIRELESS_TRANSMITTER.registerBlocks("wireless_transmitter", WirelessTransmitterBlock::new);
        DISK_MANIPULATOR.registerBlocks("disk_manipulator", DiskManipulatorBlock::new);
        CRAFTER.registerBlocks("crafter", CrafterBlock::new);
        CRAFTER_MANAGER.registerBlocks("crafter_manager", CrafterManagerBlock::new);
        CRAFTING_MONITOR.registerBlocks("crafting_monitor", CraftingMonitorBlock::new);
        DETECTOR.registerBlocks("detector", DetectorBlock::new);
    }

    private RSBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
