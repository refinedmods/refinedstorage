package com.raoulvdberge.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class RSKeyBindings {
    public static final KeyBinding FOCUS_SEARCH_BAR = new KeyBinding("key.refinedstorage.focusSearchBar", KeyConflictContext.GUI, Keyboard.KEY_TAB, "Refined Storage");
    public static final KeyBinding CLEAR_GRID_CRAFTING_MATRIX = new KeyBinding("key.refinedstorage.clearGridCraftingMatrix", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_X, "Refined Storage");

    public static void init() {
        ClientRegistry.registerKeyBinding(FOCUS_SEARCH_BAR);
        ClientRegistry.registerKeyBinding(CLEAR_GRID_CRAFTING_MATRIX);
    }
}