package com.refinedmods.refinedstorage;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public final class RSKeyBindings {
    public static final KeyMapping FOCUS_SEARCH_BAR = new KeyMapping(
        "key.refinedstorage.focusSearchBar",
        KeyConflictContext.GUI,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_TAB,
        RS.NAME
    );

    public static final KeyMapping CLEAR_GRID_CRAFTING_MATRIX = new KeyMapping(
        "key.refinedstorage.clearGridCraftingMatrix",
        KeyConflictContext.GUI,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_GRID = new KeyMapping(
        "key.refinedstorage.openWirelessGrid",
        KeyConflictContext.IN_GAME,
        InputConstants.UNKNOWN,
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_FLUID_GRID = new KeyMapping(
        "key.refinedstorage.openWirelessFluidGrid",
        KeyConflictContext.IN_GAME,
        InputConstants.UNKNOWN,
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_CRAFTING_MONITOR = new KeyMapping(
        "key.refinedstorage.openWirelessCraftingMonitor",
        KeyConflictContext.IN_GAME,
        InputConstants.UNKNOWN,
        RS.NAME
    );

    public static final KeyMapping OPEN_PORTABLE_GRID = new KeyMapping(
        "key.refinedstorage.openPortableGrid",
        KeyConflictContext.IN_GAME,
        InputConstants.UNKNOWN,
        RS.NAME
    );

    private RSKeyBindings() {
    }
}
