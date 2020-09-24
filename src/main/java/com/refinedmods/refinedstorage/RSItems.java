package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.*;
import com.refinedmods.refinedstorage.item.blockitem.*;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RS.ID);

    public static final RegistryObject<QuartzEnrichedIronItem> QUARTZ_ENRICHED_IRON;
    public static final RegistryObject<SiliconItem> SILICON;
    public static final RegistryObject<ProcessorBindingItem> PROCESSOR_BINDING;
    public static final RegistryObject<WrenchItem> WRENCH;
    public static final RegistryObject<PatternItem> PATTERN;
    public static final RegistryObject<FilterItem> FILTER;
    public static final RegistryObject<StorageHousingItem> STORAGE_HOUSING;
    public static final RegistryObject<NetworkCardItem> NETWORK_CARD;
    public static final RegistryObject<SecurityCardItem> SECURITY_CARD;
    public static final RegistryObject<CoreItem> CONSTRUCTION_CORE;
    public static final RegistryObject<CoreItem> DESTRUCTION_CORE;
    public static final RegistryObject<WirelessGridItem> WIRELESS_GRID;
    public static final RegistryObject<WirelessGridItem> CREATIVE_WIRELESS_GRID;
    public static final RegistryObject<WirelessFluidGridItem> WIRELESS_FLUID_GRID;
    public static final RegistryObject<WirelessFluidGridItem> CREATIVE_WIRELESS_FLUID_GRID;
    public static final RegistryObject<PortableGridBlockItem> PORTABLE_GRID;
    public static final RegistryObject<PortableGridBlockItem> CREATIVE_PORTABLE_GRID;
    public static final RegistryObject<WirelessCraftingMonitorItem> WIRELESS_CRAFTING_MONITOR;
    public static final RegistryObject<WirelessCraftingMonitorItem> CREATIVE_WIRELESS_CRAFTING_MONITOR;
    public static final RegistryObject<BlockItem> MACHINE_CASING;

    public static final Map<ProcessorItem.Type, RegistryObject<ProcessorItem>> PROCESSORS = new HashMap<>();

    public static final Map<ItemStorageType, RegistryObject<StoragePartItem>> ITEM_STORAGE_PARTS = new HashMap<>();
    public static final Map<ItemStorageType, RegistryObject<StorageDiskItem>> ITEM_STORAGE_DISKS = new HashMap<>();
    public static final Map<ItemStorageType, RegistryObject<StorageBlockItem>> STORAGE_BLOCKS = new HashMap<>();

    public static final Map<FluidStorageType, RegistryObject<FluidStoragePartItem>> FLUID_STORAGE_PARTS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageDiskItem>> FLUID_STORAGE_DISKS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageBlockItem>> FLUID_STORAGE_BLOCKS = new HashMap<>();

    public static final Map<UpgradeItem.Type, RegistryObject<UpgradeItem>> UPGRADE_ITEMS = new HashMap<>();

    public static final ColorMap<BlockItem> CRAFTER = new ColorMap<>();
    public static final ColorMap<BlockItem> RELAY = new ColorMap<>();
    public static final ColorMap<BlockItem> NETWORK_TRANSMITTER = new ColorMap<>();
    public static final ColorMap<BlockItem> NETWORK_RECEIVER = new ColorMap<>();
    public static final ColorMap<BlockItem> CONTROLLER = new ColorMap<>();
    public static final ColorMap<BlockItem> CREATIVE_CONTROLLER = new ColorMap<>();
    public static final ColorMap<BlockItem> GRID = new ColorMap<>();
    public static final ColorMap<BlockItem> CRAFTING_GRID = new ColorMap<>();
    public static final ColorMap<BlockItem> PATTERN_GRID = new ColorMap<>();
    public static final ColorMap<BlockItem> FLUID_GRID = new ColorMap<>();
    public static final ColorMap<BlockItem> SECURITY_MANAGER = new ColorMap<>();
    public static final ColorMap<BlockItem> WIRELESS_TRANSMITTER = new ColorMap<>();
    public static final ColorMap<BlockItem> DISK_MANIPULATOR = new ColorMap<>();
    public static final ColorMap<BlockItem> CRAFTER_MANAGER = new ColorMap<>();
    public static final ColorMap<BlockItem> CRAFTING_MONITOR = new ColorMap<>();
    public static final ColorMap<BlockItem> DETECTOR = new ColorMap<>();

    public static final Map<Tags.IOptionalNamedTag<Item>, ColorMap<BlockItem>> COLORED_ITEM_TAGS = new HashMap<>();

    public static final List<Runnable> LATE_REGISTRATION = new ArrayList<>();

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

            ITEM_STORAGE_DISKS.put(type, ITEMS.register(type.getName() + "_storage_disk", () -> new StorageDiskItem(type)));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            if (type != FluidStorageType.CREATIVE) {
                FLUID_STORAGE_PARTS.put(type, ITEMS.register(type.getName() + "_fluid_storage_part", FluidStoragePartItem::new));
            }

            FLUID_STORAGE_DISKS.put(type, ITEMS.register(type.getName() + "_fluid_storage_disk", () -> new FluidStorageDiskItem(type)));
        }

        STORAGE_HOUSING = ITEMS.register("storage_housing", StorageHousingItem::new);

        for (UpgradeItem.Type type : UpgradeItem.Type.values()) {
            UPGRADE_ITEMS.put(type, ITEMS.register(type == UpgradeItem.Type.NORMAL ? "upgrade" : type.getName() + "_upgrade", () -> new UpgradeItem(type)));
        }

        WRENCH = ITEMS.register("wrench", WrenchItem::new);
        PATTERN = ITEMS.register("pattern", PatternItem::new);
        FILTER = ITEMS.register("filter", FilterItem::new);
        PORTABLE_GRID = ITEMS.register("portable_grid", () -> new PortableGridBlockItem(PortableGridBlockItem.Type.NORMAL));
        CREATIVE_PORTABLE_GRID = ITEMS.register("creative_portable_grid", () -> new PortableGridBlockItem(PortableGridBlockItem.Type.CREATIVE));

        registerBlockItemFor(RSBlocks.QUARTZ_ENRICHED_IRON);
        MACHINE_CASING = registerBlockItemFor(RSBlocks.MACHINE_CASING);
        registerBlockItemFor(RSBlocks.CABLE);
        registerBlockItemFor(RSBlocks.DISK_DRIVE);

        for (ItemStorageType type : ItemStorageType.values()) {
            STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.STORAGE_BLOCKS.get(type).getId().getPath(), () -> new StorageBlockItem(RSBlocks.STORAGE_BLOCKS.get(type).get())));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            FLUID_STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).getId().getPath(), () -> new FluidStorageBlockItem(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).get())));
        }

        registerBlockItemFor(RSBlocks.EXTERNAL_STORAGE);
        registerBlockItemFor(RSBlocks.IMPORTER);
        registerBlockItemFor(RSBlocks.EXPORTER);
        registerBlockItemFor(RSBlocks.INTERFACE);
        registerBlockItemFor(RSBlocks.FLUID_INTERFACE);
        registerBlockItemFor(RSBlocks.STORAGE_MONITOR);
        registerBlockItemFor(RSBlocks.CONSTRUCTOR);
        registerBlockItemFor(RSBlocks.DESTRUCTOR);

        CONTROLLER.put(BlockUtils.DEFAULT_COLOR, ITEMS.register(RSBlocks.CONTROLLER.get(BlockUtils.DEFAULT_COLOR).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CONTROLLER.get(BlockUtils.DEFAULT_COLOR).get(), BlockUtils.DEFAULT_COLOR, RSBlocks.CONTROLLER.get(BlockUtils.DEFAULT_COLOR))));
        CREATIVE_CONTROLLER.put(BlockUtils.DEFAULT_COLOR, ITEMS.register(RSBlocks.CREATIVE_CONTROLLER.get(BlockUtils.DEFAULT_COLOR).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER.get(BlockUtils.DEFAULT_COLOR).get(), BlockUtils.DEFAULT_COLOR, RSBlocks.CREATIVE_CONTROLLER.get(BlockUtils.DEFAULT_COLOR))));

        COLORED_ITEM_TAGS.put(ItemTags.createOptional(new ResourceLocation(RS.ID, CONTROLLER.get(BlockUtils.DEFAULT_COLOR).getId().getPath())), CONTROLLER);

        LATE_REGISTRATION.add(() -> {
            RSBlocks.CONTROLLER.forEach((color, block) -> {
                if (color != BlockUtils.DEFAULT_COLOR) {
                    CONTROLLER.put(color, ITEMS.register(RSBlocks.CONTROLLER.get(color).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CONTROLLER.get(color).get(), color, RSBlocks.CONTROLLER.get(BlockUtils.DEFAULT_COLOR))));
                }
            });

            RSBlocks.CREATIVE_CONTROLLER.forEach((color, block) -> {
                if (color != BlockUtils.DEFAULT_COLOR) {
                    CREATIVE_CONTROLLER.put(color, ITEMS.register(RSBlocks.CREATIVE_CONTROLLER.get(color).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER.get(color).get(), color, RSBlocks.CREATIVE_CONTROLLER.get(BlockUtils.DEFAULT_COLOR))));
                }
            });
        });

        GRID.registerColoredItemsFromBlocks(RSBlocks.GRID);
        CRAFTING_GRID.registerColoredItemsFromBlocks(RSBlocks.CRAFTING_GRID);
        PATTERN_GRID.registerColoredItemsFromBlocks(RSBlocks.PATTERN_GRID);
        FLUID_GRID.registerColoredItemsFromBlocks(RSBlocks.FLUID_GRID);
        NETWORK_RECEIVER.registerColoredItemsFromBlocks(RSBlocks.NETWORK_RECEIVER);
        NETWORK_TRANSMITTER.registerColoredItemsFromBlocks(RSBlocks.NETWORK_TRANSMITTER);
        RELAY.registerColoredItemsFromBlocks(RSBlocks.RELAY);
        DETECTOR.registerColoredItemsFromBlocks(RSBlocks.DETECTOR);
        SECURITY_MANAGER.registerColoredItemsFromBlocks(RSBlocks.SECURITY_MANAGER);
        WIRELESS_TRANSMITTER.registerColoredItemsFromBlocks(RSBlocks.WIRELESS_TRANSMITTER);
        DISK_MANIPULATOR.registerColoredItemsFromBlocks(RSBlocks.DISK_MANIPULATOR);
        CRAFTER.registerColoredItemsFromBlocks(RSBlocks.CRAFTER);
        CRAFTER_MANAGER.registerColoredItemsFromBlocks(RSBlocks.CRAFTER_MANAGER);
        CRAFTING_MONITOR.registerColoredItemsFromBlocks(RSBlocks.CRAFTING_MONITOR);

        WIRELESS_GRID = ITEMS.register("wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_GRID = ITEMS.register("creative_wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.CREATIVE));
        WIRELESS_FLUID_GRID = ITEMS.register("wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_FLUID_GRID = ITEMS.register("creative_wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.CREATIVE));
        WIRELESS_CRAFTING_MONITOR = ITEMS.register("wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.NORMAL));
        CREATIVE_WIRELESS_CRAFTING_MONITOR = ITEMS.register("creative_wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.CREATIVE));

        LATE_REGISTRATION.forEach(Runnable::run);
    }

    private static <T extends BaseBlock> RegistryObject<BlockItem> registerBlockItemFor(RegistryObject<T> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BaseBlockItem(block.get(), new Item.Properties().group(RS.MAIN_GROUP)));
    }

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
