package com.raoulvdberge.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class RSKeyBindings {
    public static final KeyBinding FOCUS_SEARCH_BAR = new KeyBinding("key.refinedstorage.focusSearchBar", KeyConflictContext.GUI, Keyboard.KEY_TAB, "Refined Storage");
    public static final KeyBinding CLEAR_GRID_CRAFTING_MATRIX = new KeyBinding("key.refinedstorage.clearGridCraftingMatrix", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_X, "Refined Storage");
    public static final KeyBinding OPEN_WIRELESS_GRID = new KeyBinding("key.refinedstorage.openWirelessGrid", KeyConflictContext.IN_GAME, 0, "Refined Storage");
    public static final KeyBinding OPEN_WIRELESS_FLUID_GRID = new KeyBinding("key.refinedstorage.openWirelessFluidGrid", KeyConflictContext.IN_GAME, 0, "Refined Storage");
    public static final KeyBinding OPEN_PORTABLE_GRID = new KeyBinding("key.refinedstorage.openPortableGrid", KeyConflictContext.IN_GAME, 0, "Refined Storage");
    public static final KeyBinding OPEN_WIRELESS_CRAFTING_MONITOR = new KeyBinding("key.refinedstorage.openWirelessCraftingMonitor", KeyConflictContext.IN_GAME, 0, "Refined Storage");

    public static void init() {
        ClientRegistry.registerKeyBinding(FOCUS_SEARCH_BAR);
        ClientRegistry.registerKeyBinding(CLEAR_GRID_CRAFTING_MATRIX);
        ClientRegistry.registerKeyBinding(OPEN_WIRELESS_GRID);
        ClientRegistry.registerKeyBinding(OPEN_WIRELESS_FLUID_GRID);
        ClientRegistry.registerKeyBinding(OPEN_PORTABLE_GRID);
        ClientRegistry.registerKeyBinding(OPEN_WIRELESS_CRAFTING_MONITOR);
    }
}