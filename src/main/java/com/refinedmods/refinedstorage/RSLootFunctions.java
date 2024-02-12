package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public final class RSLootFunctions {
    public static LootItemFunctionType STORAGE_BLOCK;
    public static LootItemFunctionType PORTABLE_GRID;
    public static LootItemFunctionType CRAFTER;
    public static LootItemFunctionType CONTROLLER;

    private RSLootFunctions() {
    }

    public static void register() {
        STORAGE_BLOCK =
            Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "storage_block"),
                new LootItemFunctionType(Codec.unit(new StorageBlockLootFunction())));
        PORTABLE_GRID =
            Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "portable_grid"),
                new LootItemFunctionType(
                    Codec.unit(new PortableGridBlockLootFunction())));
        CRAFTER = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "crafter"),
            new LootItemFunctionType(Codec.unit(new CrafterLootFunction())));
        CONTROLLER = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(RS.ID, "controller"),
            new LootItemFunctionType(Codec.unit(new ControllerLootFunction())));
    }
}
