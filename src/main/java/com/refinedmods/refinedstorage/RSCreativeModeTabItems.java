package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.item.*;
import com.refinedmods.refinedstorage.util.ColorMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

public class RSCreativeModeTabItems {
    private RSCreativeModeTabItems() {
    }

    public static void register(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        helper.register(new ResourceLocation(RS.ID, "general"), CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.refinedstorage"))
            .icon(() -> new ItemStack(RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get()))
            .displayItems((params, output) -> RSCreativeModeTabItems.append(output))
            .build());
    }

    public static void append(CreativeModeTab.Output output) {
        add(output, RSItems.CONSTRUCTION_CORE);
        add(output, RSItems.DESTRUCTION_CORE);
        add(output, RSItems.QUARTZ_ENRICHED_IRON);
        add(output, RSItems.PROCESSOR_BINDING);
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.RAW_BASIC));
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.RAW_IMPROVED));
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.RAW_ADVANCED));
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.BASIC));
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.IMPROVED));
        add(output, RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED));
        add(output, RSItems.SILICON);
        add(output, RSItems.SECURITY_CARD);
        add(output, RSItems.NETWORK_CARD);

        add(output, ItemStorageType.ONE_K);
        add(output, ItemStorageType.FOUR_K);
        add(output, ItemStorageType.SIXTEEN_K);
        add(output, ItemStorageType.SIXTY_FOUR_K);
        add(output, ItemStorageType.CREATIVE);

        add(output, FluidStorageType.SIXTY_FOUR_K);
        add(output, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K);
        add(output, FluidStorageType.THOUSAND_TWENTY_FOUR_K);
        add(output, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K);
        add(output, FluidStorageType.CREATIVE);

        add(output, RSItems.STORAGE_HOUSING);

        add(output, UpgradeItem.Type.NORMAL);
        add(output, UpgradeItem.Type.SPEED);
        add(output, UpgradeItem.Type.RANGE);
        add(output, UpgradeItem.Type.CRAFTING);
        add(output, UpgradeItem.Type.STACK);
        add(output, UpgradeItem.Type.SILK_TOUCH);
        add(output, UpgradeItem.Type.FORTUNE_1);
        add(output, UpgradeItem.Type.FORTUNE_2);
        add(output, UpgradeItem.Type.FORTUNE_3);
        add(output, UpgradeItem.Type.REGULATOR);

        add(output, RSItems.WRENCH);
        add(output, RSItems.PATTERN);
        add(output, RSItems.FILTER);

        add(output, RSItems.WIRELESS_GRID);
        add(output, RSItems.CREATIVE_WIRELESS_GRID);
        add(output, RSItems.WIRELESS_FLUID_GRID);
        add(output, RSItems.CREATIVE_WIRELESS_FLUID_GRID);
        add(output, RSItems.WIRELESS_CRAFTING_MONITOR);
        add(output, RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR);

        add(output, RSItems.PORTABLE_GRID);
        add(output, RSItems.CREATIVE_PORTABLE_GRID);
        add(output, RSItems.QUARTZ_ENRICHED_IRON_BLOCK);
        add(output, RSItems.MACHINE_CASING);
        add(output, RSItems.CABLE);
        add(output, RSItems.DISK_DRIVE);

        addStorageBlock(output, ItemStorageType.ONE_K);
        addStorageBlock(output, ItemStorageType.FOUR_K);
        addStorageBlock(output, ItemStorageType.SIXTEEN_K);
        addStorageBlock(output, ItemStorageType.SIXTY_FOUR_K);
        addStorageBlock(output, ItemStorageType.CREATIVE);

        addStorageBlock(output, FluidStorageType.SIXTY_FOUR_K);
        addStorageBlock(output, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K);
        addStorageBlock(output, FluidStorageType.THOUSAND_TWENTY_FOUR_K);
        addStorageBlock(output, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K);
        addStorageBlock(output, FluidStorageType.CREATIVE);

        add(output, RSItems.EXTERNAL_STORAGE);
        add(output, RSItems.IMPORTER);
        add(output, RSItems.EXPORTER);
        add(output, RSItems.INTERFACE);
        add(output, RSItems.FLUID_INTERFACE);
        add(output, RSItems.STORAGE_MONITOR);
        add(output, RSItems.CONSTRUCTOR);
        add(output, RSItems.DESTRUCTOR);
        add(output, RSItems.CONTROLLER);
        add(output, RSItems.CREATIVE_CONTROLLER);
        add(output, RSItems.GRID);
        add(output, RSItems.CRAFTING_GRID);
        add(output, RSItems.PATTERN_GRID);
        add(output, RSItems.FLUID_GRID);
        add(output, RSItems.NETWORK_RECEIVER);
        add(output, RSItems.NETWORK_TRANSMITTER);
        add(output, RSItems.RELAY);
        add(output, RSItems.DETECTOR);
        add(output, RSItems.SECURITY_MANAGER);
        add(output, RSItems.WIRELESS_TRANSMITTER);
        add(output, RSItems.DISK_MANIPULATOR);
        add(output, RSItems.CRAFTER);
        add(output, RSItems.CRAFTER_MANAGER);
        add(output, RSItems.CRAFTING_MONITOR);

        addCovers(output);
    }

    private static void addCovers(CreativeModeTab.Output output) {
        if (!RS.CLIENT_CONFIG.getCover().showAllRecipesInJEI()) {
            ItemStack coverExampleStack = new ItemStack(Blocks.STONE_BRICKS);
            addCovers(output, coverExampleStack);
            return;
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            Item item = Item.byBlock(block);
            if (item == Items.AIR) {
                continue;
            }
            ItemStack itemStack = new ItemStack(item);
            if (CoverManager.isValidCover(itemStack)) {
                addCovers(output, itemStack);
            }
        }
    }

    private static void addCovers(CreativeModeTab.Output output, ItemStack coverExampleStack) {
        ItemStack coverStack = new ItemStack(RSItems.COVER.get());
        ItemStack hollowCoverStack = new ItemStack(RSItems.HOLLOW_COVER.get());
        CoverItem.setItem(coverStack, coverExampleStack);
        CoverItem.setItem(hollowCoverStack, coverExampleStack);
        output.accept(coverStack);
        output.accept(hollowCoverStack);
    }

    private static void add(CreativeModeTab.Output output, ColorMap<Item, ? extends Item> cm) {
        cm.values().forEach(c -> add(output, c));
    }

    private static void add(CreativeModeTab.Output output, ItemStorageType type) {
        DeferredHolder<Item, StoragePartItem> part = RSItems.ITEM_STORAGE_PARTS.get(type);
        if (part != null) {
            add(output, part);
        }
        add(output, RSItems.ITEM_STORAGE_DISKS.get(type));
    }

    private static void addStorageBlock(CreativeModeTab.Output output, ItemStorageType type) {
        add(output, RSItems.STORAGE_BLOCKS.get(type));
    }

    private static void addStorageBlock(CreativeModeTab.Output output, FluidStorageType type) {
        add(output, RSItems.FLUID_STORAGE_BLOCKS.get(type));
    }

    private static void add(CreativeModeTab.Output output, FluidStorageType type) {
        DeferredHolder<Item, FluidStoragePartItem> part = RSItems.FLUID_STORAGE_PARTS.get(type);
        if (part != null) {
            add(output, part);
        }
        add(output, RSItems.FLUID_STORAGE_DISKS.get(type));
    }

    private static void add(CreativeModeTab.Output output, UpgradeItem.Type type) {
        add(output, RSItems.UPGRADE_ITEMS.get(type));
    }

    private static void add(CreativeModeTab.Output output, DeferredHolder<Item, ? extends Item> ro) {
        output.accept(ro.get());
    }
}
