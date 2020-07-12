package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class RSLootFunctions {
    public static final LootFunctionType STORAGE_BLOCK = Registry.register(Registry.field_239694_aZ_, new ResourceLocation(RS.ID, "storage_block"), new LootFunctionType(new StorageBlockLootFunction.Serializer()));
    public static final LootFunctionType PORTABLE_GRID = Registry.register(Registry.field_239694_aZ_, new ResourceLocation(RS.ID, "portable_grid"), new LootFunctionType(new PortableGridBlockLootFunction.Serializer()));
    public static final LootFunctionType CRAFTER = Registry.register(Registry.field_239694_aZ_, new ResourceLocation(RS.ID, "crafter"), new LootFunctionType(new CrafterLootFunction.Serializer()));
    public static final LootFunctionType CONTROLLER = Registry.register(Registry.field_239694_aZ_, new ResourceLocation(RS.ID, "controller"), new LootFunctionType(new ControllerLootFunction.Serializer()));
}
