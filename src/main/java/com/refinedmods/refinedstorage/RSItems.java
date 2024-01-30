package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.CoreItem;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.item.FluidStorageDiskItem;
import com.refinedmods.refinedstorage.item.FluidStoragePartItem;
import com.refinedmods.refinedstorage.item.HollowCoverItem;
import com.refinedmods.refinedstorage.item.NetworkCardItem;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.item.ProcessorBindingItem;
import com.refinedmods.refinedstorage.item.ProcessorItem;
import com.refinedmods.refinedstorage.item.QuartzEnrichedIronItem;
import com.refinedmods.refinedstorage.item.SecurityCardItem;
import com.refinedmods.refinedstorage.item.SiliconItem;
import com.refinedmods.refinedstorage.item.StorageDiskItem;
import com.refinedmods.refinedstorage.item.StorageHousingItem;
import com.refinedmods.refinedstorage.item.StoragePartItem;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem;
import com.refinedmods.refinedstorage.item.WirelessFluidGridItem;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.item.WrenchItem;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import com.refinedmods.refinedstorage.item.blockitem.ControllerBlockItem;
import com.refinedmods.refinedstorage.item.blockitem.FluidStorageBlockItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.item.blockitem.StorageBlockItem;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.ItemColorMap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RS.ID);

    public static final DeferredHolder<Item, QuartzEnrichedIronItem> QUARTZ_ENRICHED_IRON;
    public static final DeferredHolder<Item, SiliconItem> SILICON;
    public static final DeferredHolder<Item, ProcessorBindingItem> PROCESSOR_BINDING;
    public static final DeferredHolder<Item, WrenchItem> WRENCH;
    public static final DeferredHolder<Item, PatternItem> PATTERN;
    public static final DeferredHolder<Item, FilterItem> FILTER;
    public static final DeferredHolder<Item, StorageHousingItem> STORAGE_HOUSING;
    public static final DeferredHolder<Item, NetworkCardItem> NETWORK_CARD;
    public static final DeferredHolder<Item, SecurityCardItem> SECURITY_CARD;
    public static final DeferredHolder<Item, CoreItem> CONSTRUCTION_CORE;
    public static final DeferredHolder<Item, CoreItem> DESTRUCTION_CORE;
    public static final DeferredHolder<Item, WirelessGridItem> WIRELESS_GRID;
    public static final DeferredHolder<Item, WirelessGridItem> CREATIVE_WIRELESS_GRID;
    public static final DeferredHolder<Item, WirelessFluidGridItem> WIRELESS_FLUID_GRID;
    public static final DeferredHolder<Item, WirelessFluidGridItem> CREATIVE_WIRELESS_FLUID_GRID;
    public static final DeferredHolder<Item, PortableGridBlockItem> PORTABLE_GRID;
    public static final DeferredHolder<Item, PortableGridBlockItem> CREATIVE_PORTABLE_GRID;
    public static final DeferredHolder<Item, WirelessCraftingMonitorItem> WIRELESS_CRAFTING_MONITOR;
    public static final DeferredHolder<Item, WirelessCraftingMonitorItem> CREATIVE_WIRELESS_CRAFTING_MONITOR;
    public static final DeferredHolder<Item, BlockItem> MACHINE_CASING;
    public static final DeferredHolder<Item, CoverItem> COVER;
    public static final DeferredHolder<Item, CoverItem> HOLLOW_COVER;
    public static final DeferredHolder<Item, BlockItem> QUARTZ_ENRICHED_IRON_BLOCK;
    public static final DeferredHolder<Item, BlockItem> CABLE;
    public static final DeferredHolder<Item, BlockItem> DISK_DRIVE;
    public static final DeferredHolder<Item, BlockItem> EXTERNAL_STORAGE;
    public static final DeferredHolder<Item, BlockItem> IMPORTER;
    public static final DeferredHolder<Item, BlockItem> EXPORTER;
    public static final DeferredHolder<Item, BlockItem> INTERFACE;
    public static final DeferredHolder<Item, BlockItem> FLUID_INTERFACE;
    public static final DeferredHolder<Item, BlockItem> STORAGE_MONITOR;
    public static final DeferredHolder<Item, BlockItem> CONSTRUCTOR;
    public static final DeferredHolder<Item, BlockItem> DESTRUCTOR;

    public static final Map<ProcessorItem.Type, DeferredHolder<Item, ProcessorItem>> PROCESSORS =
        new EnumMap<>(ProcessorItem.Type.class);

    public static final Map<ItemStorageType, DeferredHolder<Item, StoragePartItem>> ITEM_STORAGE_PARTS =
        new EnumMap<>(ItemStorageType.class);
    public static final Map<ItemStorageType, DeferredHolder<Item, StorageDiskItem>> ITEM_STORAGE_DISKS =
        new EnumMap<>(ItemStorageType.class);
    public static final Map<ItemStorageType, DeferredHolder<Item, StorageBlockItem>> STORAGE_BLOCKS =
        new EnumMap<>(ItemStorageType.class);

    public static final Map<FluidStorageType, DeferredHolder<Item, FluidStoragePartItem>> FLUID_STORAGE_PARTS =
        new EnumMap<>(FluidStorageType.class);
    public static final Map<FluidStorageType, DeferredHolder<Item, FluidStorageDiskItem>> FLUID_STORAGE_DISKS =
        new EnumMap<>(FluidStorageType.class);
    public static final Map<FluidStorageType, DeferredHolder<Item, FluidStorageBlockItem>> FLUID_STORAGE_BLOCKS =
        new EnumMap<>(FluidStorageType.class);

    public static final Map<UpgradeItem.Type, DeferredHolder<Item, UpgradeItem>> UPGRADE_ITEMS =
        new EnumMap<>(UpgradeItem.Type.class);

    public static final Map<TagKey<Item>, ColorMap<Item, ?>> COLORED_ITEM_TAGS = new HashMap<>();

    private static final List<Runnable> LATE_REGISTRATION = new ArrayList<>();

    public static final ItemColorMap CRAFTER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap RELAY = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap NETWORK_TRANSMITTER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap NETWORK_RECEIVER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ColorMap<Item, ControllerBlockItem> CONTROLLER = new ColorMap<>(ITEMS, LATE_REGISTRATION);
    public static final ColorMap<Item, ControllerBlockItem> CREATIVE_CONTROLLER = new ColorMap<>(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap GRID = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap CRAFTING_GRID = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap PATTERN_GRID = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap FLUID_GRID = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap SECURITY_MANAGER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap WIRELESS_TRANSMITTER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap DISK_MANIPULATOR = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap CRAFTER_MANAGER = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap CRAFTING_MONITOR = new ItemColorMap(ITEMS, LATE_REGISTRATION);
    public static final ItemColorMap DETECTOR = new ItemColorMap(ITEMS, LATE_REGISTRATION);

    static {
        CONSTRUCTION_CORE = ITEMS.register("construction_core", CoreItem::new);
        DESTRUCTION_CORE = ITEMS.register("destruction_core", CoreItem::new);
        QUARTZ_ENRICHED_IRON = ITEMS.register("quartz_enriched_iron", QuartzEnrichedIronItem::new);
        PROCESSOR_BINDING = ITEMS.register("processor_binding", ProcessorBindingItem::new);

        for (ProcessorItem.Type type : ProcessorItem.Type.values()) {
            PROCESSORS.put(type, ITEMS.register(type.getName() + "_processor", ProcessorItem::new));
        }

        SILICON = ITEMS.register("silicon", SiliconItem::new);
        SECURITY_CARD = ITEMS.register("security_card", SecurityCardItem::new);
        NETWORK_CARD = ITEMS.register("network_card", NetworkCardItem::new);

        for (ItemStorageType type : ItemStorageType.values()) {
            if (type != ItemStorageType.CREATIVE) {
                ITEM_STORAGE_PARTS.put(type, ITEMS.register(type.getName() + "_storage_part", StoragePartItem::new));
            }

            ITEM_STORAGE_DISKS.put(type,
                ITEMS.register(type.getName() + "_storage_disk", () -> new StorageDiskItem(type)));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            if (type != FluidStorageType.CREATIVE) {
                FLUID_STORAGE_PARTS.put(type,
                    ITEMS.register(type.getName() + "_fluid_storage_part", FluidStoragePartItem::new));
            }

            FLUID_STORAGE_DISKS.put(type,
                ITEMS.register(type.getName() + "_fluid_storage_disk", () -> new FluidStorageDiskItem(type)));
        }

        STORAGE_HOUSING = ITEMS.register("storage_housing", StorageHousingItem::new);

        for (UpgradeItem.Type type : UpgradeItem.Type.values()) {
            UPGRADE_ITEMS.put(type,
                ITEMS.register(type == UpgradeItem.Type.NORMAL ? "upgrade" : type.getName() + "_upgrade",
                    () -> new UpgradeItem(type)));
        }

        WRENCH = ITEMS.register("wrench", WrenchItem::new);
        PATTERN = ITEMS.register("pattern", PatternItem::new);
        FILTER = ITEMS.register("filter", FilterItem::new);
        PORTABLE_GRID =
            ITEMS.register("portable_grid", () -> new PortableGridBlockItem(PortableGridBlockItem.Type.NORMAL));
        CREATIVE_PORTABLE_GRID = ITEMS.register("creative_portable_grid",
            () -> new PortableGridBlockItem(PortableGridBlockItem.Type.CREATIVE));

        QUARTZ_ENRICHED_IRON_BLOCK = registerBlockItemFor(RSBlocks.QUARTZ_ENRICHED_IRON);
        MACHINE_CASING = registerBlockItemFor(RSBlocks.MACHINE_CASING);
        COVER = ITEMS.register("cover", CoverItem::new);
        HOLLOW_COVER = ITEMS.register("hollow_cover", HollowCoverItem::new);
        CABLE = registerBlockItemFor(RSBlocks.CABLE);
        DISK_DRIVE = registerBlockItemFor(RSBlocks.DISK_DRIVE);

        for (ItemStorageType type : ItemStorageType.values()) {
            STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.STORAGE_BLOCKS.get(type).getId().getPath(),
                () -> new StorageBlockItem(RSBlocks.STORAGE_BLOCKS.get(type).get())));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            FLUID_STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).getId().getPath(),
                () -> new FluidStorageBlockItem(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).get())));
        }

        EXTERNAL_STORAGE = registerBlockItemFor(RSBlocks.EXTERNAL_STORAGE);
        IMPORTER = registerBlockItemFor(RSBlocks.IMPORTER);
        EXPORTER = registerBlockItemFor(RSBlocks.EXPORTER);
        INTERFACE = registerBlockItemFor(RSBlocks.INTERFACE);
        FLUID_INTERFACE = registerBlockItemFor(RSBlocks.FLUID_INTERFACE);
        STORAGE_MONITOR = registerBlockItemFor(RSBlocks.STORAGE_MONITOR);
        CONSTRUCTOR = registerBlockItemFor(RSBlocks.CONSTRUCTOR);
        DESTRUCTOR = registerBlockItemFor(RSBlocks.DESTRUCTOR);

        CONTROLLER.put(ColorMap.DEFAULT_COLOR, ITEMS.register(
            RSBlocks.CONTROLLER.get(ColorMap.DEFAULT_COLOR).getId().getPath(),
            () -> new ControllerBlockItem(
                RSBlocks.CONTROLLER.get(ColorMap.DEFAULT_COLOR).get(),
                ColorMap.DEFAULT_COLOR,
                BlockUtils.getBlockTranslation(RSBlocks.CONTROLLER.get(ColorMap.DEFAULT_COLOR).get())
            )
        ));
        CREATIVE_CONTROLLER.put(ColorMap.DEFAULT_COLOR, ITEMS.register(
            RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).getId().getPath(),
            () -> new ControllerBlockItem(
                RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get(),
                ColorMap.DEFAULT_COLOR,
                BlockUtils.getBlockTranslation(RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get())
            )
        ));

        COLORED_ITEM_TAGS.put(
            ItemTags.create(new ResourceLocation(RS.ID, CONTROLLER.get(ColorMap.DEFAULT_COLOR).getId().getPath())),
            CONTROLLER
        );

        LATE_REGISTRATION.add(() -> {
            RSBlocks.CONTROLLER.forEach((color, block) -> {
                if (color != ColorMap.DEFAULT_COLOR) {
                    CONTROLLER.put(color, ITEMS.register(RSBlocks.CONTROLLER.get(color).getId().getPath(),
                        () -> new ControllerBlockItem(RSBlocks.CONTROLLER.get(color).get(), color,
                            Component.translatable(
                                RSBlocks.CONTROLLER.get(ColorMap.DEFAULT_COLOR).get().getDescriptionId()))));
                }
            });

            RSBlocks.CREATIVE_CONTROLLER.forEach((color, block) -> {
                if (color != ColorMap.DEFAULT_COLOR) {
                    CREATIVE_CONTROLLER.put(color,
                        ITEMS.register(RSBlocks.CREATIVE_CONTROLLER.get(color).getId().getPath(),
                            () -> new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER.get(color).get(), color,
                                Component.translatable(RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get()
                                    .getDescriptionId()))));
                }
            });
        });

        GRID.registerItemsFromBlocks(RSBlocks.GRID);
        CRAFTING_GRID.registerItemsFromBlocks(RSBlocks.CRAFTING_GRID);
        PATTERN_GRID.registerItemsFromBlocks(RSBlocks.PATTERN_GRID);
        FLUID_GRID.registerItemsFromBlocks(RSBlocks.FLUID_GRID);
        NETWORK_RECEIVER.registerItemsFromBlocks(RSBlocks.NETWORK_RECEIVER);
        NETWORK_TRANSMITTER.registerItemsFromBlocks(RSBlocks.NETWORK_TRANSMITTER);
        RELAY.registerItemsFromBlocks(RSBlocks.RELAY);
        DETECTOR.registerItemsFromBlocks(RSBlocks.DETECTOR);
        SECURITY_MANAGER.registerItemsFromBlocks(RSBlocks.SECURITY_MANAGER);
        WIRELESS_TRANSMITTER.registerItemsFromBlocks(RSBlocks.WIRELESS_TRANSMITTER);
        DISK_MANIPULATOR.registerItemsFromBlocks(RSBlocks.DISK_MANIPULATOR);
        CRAFTER.registerItemsFromBlocks(RSBlocks.CRAFTER);
        CRAFTER_MANAGER.registerItemsFromBlocks(RSBlocks.CRAFTER_MANAGER);
        CRAFTING_MONITOR.registerItemsFromBlocks(RSBlocks.CRAFTING_MONITOR);

        WIRELESS_GRID = ITEMS.register("wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_GRID =
            ITEMS.register("creative_wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.CREATIVE));
        WIRELESS_FLUID_GRID =
            ITEMS.register("wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_FLUID_GRID = ITEMS.register("creative_wireless_fluid_grid",
            () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.CREATIVE));
        WIRELESS_CRAFTING_MONITOR = ITEMS.register("wireless_crafting_monitor",
            () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.NORMAL));
        CREATIVE_WIRELESS_CRAFTING_MONITOR = ITEMS.register("creative_wireless_crafting_monitor",
            () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.CREATIVE));

        LATE_REGISTRATION.forEach(Runnable::run);
    }

    private RSItems() {
    }

    private static <T extends BaseBlock> DeferredHolder<Item, BlockItem> registerBlockItemFor(
        DeferredHolder<Block, T> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BaseBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
