package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class RSLootFunctions {
    private static LootFunctionType storageBlock;
    private static LootFunctionType portableGrid;
    private static LootFunctionType crafter;
    private static LootFunctionType controller;

    private RSLootFunctions() {
    }

    public static void register() {
        storageBlock = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "storage_block"), new LootFunctionType(new StorageBlockLootFunction.Serializer()));
        portableGrid = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "portable_grid"), new LootFunctionType(new PortableGridBlockLootFunction.Serializer()));
        crafter = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "crafter"), new LootFunctionType(new CrafterLootFunction.Serializer()));
        controller = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "controller"), new LootFunctionType(new ControllerLootFunction.Serializer()));
    }

    public static LootFunctionType getStorageBlock() {
        return storageBlock;
    }

    public static LootFunctionType getPortableGrid() {
        return portableGrid;
    }

    public static LootFunctionType getCrafter() {
        return crafter;
    }

    public static LootFunctionType getController() {
        return controller;
    }
}
