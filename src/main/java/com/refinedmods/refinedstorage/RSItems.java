package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.*;
import com.refinedmods.refinedstorage.item.blockitem.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
    public static final RegistryObject<ControllerBlockItem> CONTROLLER;
    public static final RegistryObject<ControllerBlockItem> CREATIVE_CONTROLLER;
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

    public static final Map<ProcessorItem.Type, RegistryObject<ProcessorItem>> PROCESSORS = new HashMap<>();

    public static final Map<ItemStorageType, RegistryObject<StoragePartItem>> ITEM_STORAGE_PARTS = new HashMap<>();
    public static final Map<ItemStorageType, RegistryObject<StorageDiskItem>> ITEM_STORAGE_DISKS = new HashMap<>();
    public static final Map<ItemStorageType, RegistryObject<StorageBlockItem>> STORAGE_BLOCKS = new HashMap<>();

    public static final Map<FluidStorageType, RegistryObject<FluidStoragePartItem>> FLUID_STORAGE_PARTS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageDiskItem>> FLUID_STORAGE_DISKS = new HashMap<>();
    public static final Map<FluidStorageType, RegistryObject<FluidStorageBlockItem>> FLUID_STORAGE_BLOCKS = new HashMap<>();

    public static final Map<UpgradeItem.Type, RegistryObject<UpgradeItem>> UPGRADE_ITEMS = new HashMap<>();

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

        createBlockItemFor(RSBlocks.QUARTZ_ENRICHED_IRON);
        CONTROLLER = ITEMS.register(RSBlocks.CONTROLLER.getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CONTROLLER.get()));
        CREATIVE_CONTROLLER = ITEMS.register(RSBlocks.CREATIVE_CONTROLLER.getId().getPath(), () -> new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER.get()));
        createBlockItemFor(RSBlocks.MACHINE_CASING);
        createBlockItemFor(RSBlocks.CABLE);
        createBlockItemFor(RSBlocks.DISK_DRIVE);
        createBlockItemFor(RSBlocks.GRID);
        createBlockItemFor(RSBlocks.CRAFTING_GRID);
        createBlockItemFor(RSBlocks.PATTERN_GRID);
        createBlockItemFor(RSBlocks.FLUID_GRID);

        for (ItemStorageType type : ItemStorageType.values()) {
            STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.STORAGE_BLOCKS.get(type).getId().getPath(), () -> new StorageBlockItem(RSBlocks.STORAGE_BLOCKS.get(type).get())));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            FLUID_STORAGE_BLOCKS.put(type, ITEMS.register(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).getId().getPath(), () -> new FluidStorageBlockItem(RSBlocks.FLUID_STORAGE_BLOCKS.get(type).get())));
        }

        createBlockItemFor(RSBlocks.EXTERNAL_STORAGE);
        createBlockItemFor(RSBlocks.IMPORTER);
        createBlockItemFor(RSBlocks.EXPORTER);
        createBlockItemFor(RSBlocks.NETWORK_RECEIVER);
        createBlockItemFor(RSBlocks.NETWORK_TRANSMITTER);
        createBlockItemFor(RSBlocks.RELAY);
        createBlockItemFor(RSBlocks.DETECTOR);
        createBlockItemFor(RSBlocks.SECURITY_MANAGER);
        createBlockItemFor(RSBlocks.INTERFACE);
        createBlockItemFor(RSBlocks.FLUID_INTERFACE);
        createBlockItemFor(RSBlocks.WIRELESS_TRANSMITTER);
        createBlockItemFor(RSBlocks.STORAGE_MONITOR);
        createBlockItemFor(RSBlocks.CONSTRUCTOR);
        createBlockItemFor(RSBlocks.DESTRUCTOR);
        createBlockItemFor(RSBlocks.DISK_MANIPULATOR);
        createBlockItemFor(RSBlocks.CRAFTER);
        createBlockItemFor(RSBlocks.CRAFTER_MANAGER);
        createBlockItemFor(RSBlocks.CRAFTING_MONITOR);

        WIRELESS_GRID = ITEMS.register("wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_GRID = ITEMS.register("creative_wireless_grid", () -> new WirelessGridItem(WirelessGridItem.Type.CREATIVE));
        WIRELESS_FLUID_GRID = ITEMS.register("wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.NORMAL));
        CREATIVE_WIRELESS_FLUID_GRID = ITEMS.register("creative_wireless_fluid_grid", () -> new WirelessFluidGridItem(WirelessFluidGridItem.Type.CREATIVE));
        WIRELESS_CRAFTING_MONITOR = ITEMS.register("wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.NORMAL));
        CREATIVE_WIRELESS_CRAFTING_MONITOR = ITEMS.register("creative_wireless_crafting_monitor", () -> new WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.CREATIVE));
    }

    private static <T extends BaseBlock> RegistryObject<BlockItem> createBlockItemFor(RegistryObject<T> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BaseBlockItem(block.get(), new Item.Properties().group(RS.MAIN_GROUP)));
    }

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
