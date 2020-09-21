package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.*;
import com.refinedmods.refinedstorage.item.blockitem.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class RSItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RS.ID);

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

    public static final Map<DyeColor, RegistryObject<BlockItem>> CRAFTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> RELAY = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> NETWORK_TRANSMITTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> NETWORK_RECEIVER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> CONTROLLER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> CREATIVE_CONTROLLER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> CRAFTING_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> PATTERN_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> FLUID_GRID = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> SECURITY_MANAGER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> WIRELESS_TRANSMITTER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> DISK_MANIPULATOR = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> CRAFTER_MANAGER = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> CRAFTING_MONITOR = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<BlockItem>> DETECTOR = new HashMap<>();

    public static final Map<Tags.IOptionalNamedTag<Item>, Map<DyeColor, RegistryObject<BlockItem>>> COLORED_ITEM_TAGS = new HashMap<>();

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

        RSBlocks.CONTROLLER.forEach((color, block) -> {
            CONTROLLER.put(color, ITEMS.register(RSBlocks.CONTROLLER.get(color).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CONTROLLER.get(color).get())));
            if (color == DyeColor.LIGHT_BLUE) {
                COLORED_ITEM_TAGS.put(ItemTags.createOptional(new ResourceLocation(RS.ID, block.getId().getPath())), CONTROLLER);
            }
        });

        RSBlocks.CREATIVE_CONTROLLER.forEach((color, block) -> {
            CREATIVE_CONTROLLER.put(color, ITEMS.register(RSBlocks.CREATIVE_CONTROLLER.get(color).getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER.get(color).get())));
        });

        registerItemsFromBlocks(RSBlocks.GRID, GRID);
        registerItemsFromBlocks(RSBlocks.CRAFTING_GRID, CRAFTING_GRID);
        registerItemsFromBlocks(RSBlocks.PATTERN_GRID, PATTERN_GRID);
        registerItemsFromBlocks(RSBlocks.FLUID_GRID, FLUID_GRID);
        registerItemsFromBlocks(RSBlocks.NETWORK_RECEIVER, NETWORK_RECEIVER);
        registerItemsFromBlocks(RSBlocks.NETWORK_TRANSMITTER, NETWORK_TRANSMITTER);
        registerItemsFromBlocks(RSBlocks.RELAY, RELAY);
        registerItemsFromBlocks(RSBlocks.DETECTOR, DETECTOR);
        registerItemsFromBlocks(RSBlocks.SECURITY_MANAGER, SECURITY_MANAGER);
        registerItemsFromBlocks(RSBlocks.WIRELESS_TRANSMITTER, WIRELESS_TRANSMITTER);
        registerItemsFromBlocks(RSBlocks.DISK_MANIPULATOR, DISK_MANIPULATOR);
        registerItemsFromBlocks(RSBlocks.CRAFTER, CRAFTER);
        registerItemsFromBlocks(RSBlocks.CRAFTER_MANAGER, CRAFTER_MANAGER);
        registerItemsFromBlocks(RSBlocks.CRAFTING_MONITOR, CRAFTING_MONITOR);

        WIRELESS_GRID = ITEMS.register("wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_GRID = ITEMS.register("creative_wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.CREATIVE));
        WIRELESS_FLUID_GRID = ITEMS.register("wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_FLUID_GRID = ITEMS.register("creative_wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.CREATIVE));
        WIRELESS_CRAFTING_MONITOR = ITEMS.register("wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.NORMAL));
        CREATIVE_WIRELESS_CRAFTING_MONITOR = ITEMS.register("creative_wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.CREATIVE));
    }

    private static <T extends BaseBlock> RegistryObject<BlockItem> registerBlockItemFor(RegistryObject<T> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BaseBlockItem(block.get(), new Item.Properties().group(RS.MAIN_GROUP)));
    }

    private static <T extends BaseBlock> void registerItemsFromBlocks(Map<DyeColor, RegistryObject<T>> blockMap, Map<DyeColor, RegistryObject<BlockItem>> itemMap) {
        blockMap.forEach((color, block) -> {
            itemMap.put(color, registerBlockItemFor(block));
            if (color == DyeColor.LIGHT_BLUE) {
                String name = block.getId().getPath();
                Tags.IOptionalNamedTag<Item> tag = ItemTags.createOptional(new ResourceLocation(RS.ID, name));
                COLORED_ITEM_TAGS.put(tag, itemMap);
            }
        });
    }

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
