package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.*;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public final class RSBlocks {
    public static final RegistryObject<ImporterBlock> IMPORTER;
    public static final RegistryObject<ExporterBlock> EXPORTER;
    public static final RegistryObject<QuartzEnrichedIronBlock> QUARTZ_ENRICHED_IRON;
    public static final RegistryObject<MachineCasingBlock> MACHINE_CASING;
    public static final RegistryObject<CableBlock> CABLE;
    public static final RegistryObject<DiskDriveBlock> DISK_DRIVE;
    public static final RegistryObject<ExternalStorageBlock> EXTERNAL_STORAGE;
    public static final Map<ItemStorageType, RegistryObject<StorageBlock>> STORAGE_BLOCKS = new EnumMap<>(ItemStorageType.class);
    public static final Map<FluidStorageType, RegistryObject<FluidStorageBlock>> FLUID_STORAGE_BLOCKS = new EnumMap<>(FluidStorageType.class);
    public static final Map<TagKey<Block>, ColorMap<? extends Block>> COLORED_BLOCK_TAGS = new HashMap<>();
    public static final RegistryObject<InterfaceBlock> INTERFACE;
    public static final RegistryObject<FluidInterfaceBlock> FLUID_INTERFACE;
    public static final RegistryObject<StorageMonitorBlock> STORAGE_MONITOR;
    public static final RegistryObject<ConstructorBlock> CONSTRUCTOR;
    public static final RegistryObject<DestructorBlock> DESTRUCTOR;
    public static final RegistryObject<PortableGridBlock> PORTABLE_GRID;
    public static final RegistryObject<PortableGridBlock> CREATIVE_PORTABLE_GRID;
    public static final List<RegistryObject<? extends Block>> COLORED_BLOCKS = new ArrayList<>();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RS.ID);
    public static final ColorMap<CrafterBlock> CRAFTER = new ColorMap<>(BLOCKS);
    public static final ColorMap<RelayBlock> RELAY = new ColorMap<>(BLOCKS);
    public static final ColorMap<NetworkTransmitterBlock> NETWORK_TRANSMITTER = new ColorMap<>(BLOCKS);
    public static final ColorMap<NetworkReceiverBlock> NETWORK_RECEIVER = new ColorMap<>(BLOCKS);
    public static final ColorMap<ControllerBlock> CONTROLLER = new ColorMap<>(BLOCKS);
    public static final ColorMap<ControllerBlock> CREATIVE_CONTROLLER = new ColorMap<>(BLOCKS);
    public static final ColorMap<GridBlock> GRID = new ColorMap<>(BLOCKS);
    public static final ColorMap<GridBlock> CRAFTING_GRID = new ColorMap<>(BLOCKS);
    public static final ColorMap<GridBlock> PATTERN_GRID = new ColorMap<>(BLOCKS);
    public static final ColorMap<GridBlock> FLUID_GRID = new ColorMap<>(BLOCKS);
    public static final ColorMap<SecurityManagerBlock> SECURITY_MANAGER = new ColorMap<>(BLOCKS);
    public static final ColorMap<WirelessTransmitterBlock> WIRELESS_TRANSMITTER = new ColorMap<>(BLOCKS);
    public static final ColorMap<DiskManipulatorBlock> DISK_MANIPULATOR = new ColorMap<>(BLOCKS);
    public static final ColorMap<CrafterManagerBlock> CRAFTER_MANAGER = new ColorMap<>(BLOCKS);
    public static final ColorMap<CraftingMonitorBlock> CRAFTING_MONITOR = new ColorMap<>(BLOCKS);
    public static final ColorMap<DetectorBlock> DETECTOR = new ColorMap<>(BLOCKS);
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

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
