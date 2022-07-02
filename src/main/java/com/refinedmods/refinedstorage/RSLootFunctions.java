package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class RSLootFunctions {
    public static final RegistryObject<LootItemFunctionType> STORAGE_BLOCK;
    public static final RegistryObject<LootItemFunctionType> PORTABLE_GRID;
    public static final RegistryObject<LootItemFunctionType> CRAFTER;
    public static final RegistryObject<LootItemFunctionType> CONTROLLER;

    private static final DeferredRegister<LootItemFunctionType> LOOT_ITEM_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, RS.ID);

    static {
        STORAGE_BLOCK = LOOT_ITEM_FUNCTIONS.register("storage_block", () -> new LootItemFunctionType(new StorageBlockLootFunction.Serializer()));
        PORTABLE_GRID = LOOT_ITEM_FUNCTIONS.register("portable_grid", () -> new LootItemFunctionType(new PortableGridBlockLootFunction.Serializer()));
        CRAFTER = LOOT_ITEM_FUNCTIONS.register("crafter", () -> new LootItemFunctionType(new CrafterLootFunction.Serializer()));
        CONTROLLER = LOOT_ITEM_FUNCTIONS.register("controller", () -> new LootItemFunctionType(new ControllerLootFunction.Serializer()));
    }

    private RSLootFunctions() {
    }

    public static void register() {
        LOOT_ITEM_FUNCTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
