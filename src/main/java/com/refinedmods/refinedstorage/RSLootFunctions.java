package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class RSLootFunctions {
    public static LootFunctionType STORAGE_BLOCK;
    public static LootFunctionType PORTABLE_GRID;
    public static LootFunctionType CRAFTER;
    public static LootFunctionType CONTROLLER;

    public static void register() {
        STORAGE_BLOCK = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "storage_block"), new LootFunctionType(new StorageBlockLootFunction.Serializer()));
        PORTABLE_GRID = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "portable_grid"), new LootFunctionType(new PortableGridBlockLootFunction.Serializer()));
        CRAFTER = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "crafter"), new LootFunctionType(new CrafterLootFunction.Serializer()));
        CONTROLLER = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "controller"), new LootFunctionType(new ControllerLootFunction.Serializer()));
    }
}
