package com.refinedmods.refinedstorage

import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
import org.lwjgl.glfw.GLFW

object RSKeyBindings {
    val FOCUS_SEARCH_BAR: KeyBinding = KeyBinding(
            "key.refinedstorage.focusSearchBar",
            KeyConflictContext.GUI,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "Refined Storage"
    )
    val CLEAR_GRID_CRAFTING_MATRIX: KeyBinding = KeyBinding(
            "key.refinedstorage.clearGridCraftingMatrix",
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "Refined Storage"
    )
    val OPEN_WIRELESS_GRID: KeyBinding = KeyBinding(
            "key.refinedstorage.openWirelessGrid",
            KeyConflictContext.IN_GAME,
            InputMappings.INPUT_INVALID,
            "Refined Storage"
    )
    val OPEN_WIRELESS_FLUID_GRID: KeyBinding = KeyBinding(
            "key.refinedstorage.openWirelessFluidGrid",
            KeyConflictContext.IN_GAME,
            InputMappings.INPUT_INVALID,
            "Refined Storage"
    )
    val OPEN_WIRELESS_CRAFTING_MONITOR: KeyBinding = KeyBinding(
            "key.refinedstorage.openWirelessCraftingMonitor",
            KeyConflictContext.IN_GAME,
            InputMappings.INPUT_INVALID,
            "Refined Storage"
    )
    val OPEN_PORTABLE_GRID: KeyBinding = KeyBinding(
            "key.refinedstorage.openPortableGrid",
            KeyConflictContext.IN_GAME,
            InputMappings.INPUT_INVALID,
            "Refined Storage"
    )
}