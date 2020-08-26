package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.loottable.ControllerLootFunction
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction
import net.minecraft.loot.LootFunctionType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RSLootFunctions {
    var STORAGE_BLOCK: LootFunctionType? = null
    var PORTABLE_GRID: LootFunctionType? = null
    var CRAFTER: LootFunctionType? = null
    var CONTROLLER: LootFunctionType? = null
    fun register() {
        STORAGE_BLOCK = Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(RS.Companion.ID, "storage_block"), LootFunctionType(StorageBlockLootFunction.Serializer()))
        PORTABLE_GRID = Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(RS.Companion.ID, "portable_grid"), LootFunctionType(PortableGridBlockLootFunction.Serializer()))
        CRAFTER = Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(RS.Companion.ID, "crafter"), LootFunctionType(CrafterLootFunction.Serializer()))
        CONTROLLER = Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(RS.Companion.ID, "controller"), LootFunctionType(ControllerLootFunction.Serializer()))
    }
}