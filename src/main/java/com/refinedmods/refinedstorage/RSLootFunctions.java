package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public final class RSLootFunctions {
    private static LootItemFunctionType storageBlock;
    private static LootItemFunctionType portableGrid;
    private static LootItemFunctionType crafter;
    private static LootItemFunctionType controller;

    private RSLootFunctions() {
    }

    public static void register() {
        storageBlock = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "storage_block"), new LootItemFunctionType(new StorageBlockLootFunction.Serializer()));
        portableGrid = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "portable_grid"), new LootItemFunctionType(new PortableGridBlockLootFunction.Serializer()));
        crafter = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "crafter"), new LootItemFunctionType(new CrafterLootFunction.Serializer()));
        controller = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "controller"), new LootItemFunctionType(new ControllerLootFunction.Serializer()));
    }

    public static LootItemFunctionType getStorageBlock() {
        return storageBlock;
    }

    public static LootItemFunctionType getPortableGrid() {
        return portableGrid;
    }

    public static LootItemFunctionType getCrafter() {
        return crafter;
    }

    public static LootItemFunctionType getController() {
        return controller;
    }
}
