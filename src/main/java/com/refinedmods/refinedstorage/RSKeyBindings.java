package com.refinedmods.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public final class RSKeyBindings {
    public static final KeyBinding FOCUS_SEARCH_BAR = new KeyBinding(
        "key.refinedstorage.focusSearchBar",
        KeyConflictContext.GUI,
        InputMappings.Type.KEYSYM,
        GLFW.GLFW_KEY_TAB,
        RS.NAME
    );

    public static final KeyBinding CLEAR_GRID_CRAFTING_MATRIX = new KeyBinding(
        "key.refinedstorage.clearGridCraftingMatrix",
        KeyConflictContext.GUI,
        KeyModifier.CONTROL,
        InputMappings.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        RS.NAME
    );

    public static final KeyBinding OPEN_WIRELESS_GRID = new KeyBinding(
        "key.refinedstorage.openWirelessGrid",
        KeyConflictContext.IN_GAME,
        InputMappings.UNKNOWN,
        RS.NAME
    );

    public static final KeyBinding OPEN_WIRELESS_FLUID_GRID = new KeyBinding(
        "key.refinedstorage.openWirelessFluidGrid",
        KeyConflictContext.IN_GAME,
        InputMappings.UNKNOWN,
        RS.NAME
    );

    public static final KeyBinding OPEN_WIRELESS_CRAFTING_MONITOR = new KeyBinding(
        "key.refinedstorage.openWirelessCraftingMonitor",
        KeyConflictContext.IN_GAME,
        InputMappings.UNKNOWN,
        RS.NAME
    );

    public static final KeyBinding OPEN_PORTABLE_GRID = new KeyBinding(
        "key.refinedstorage.openPortableGrid",
        KeyConflictContext.IN_GAME,
        InputMappings.UNKNOWN,
        RS.NAME
    );

    private RSKeyBindings() {
    }
}
