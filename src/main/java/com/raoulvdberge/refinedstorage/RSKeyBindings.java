package com.raoulvdberge.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class RSKeyBindings {
    public static final KeyBinding FOCUS_SEARCH_BAR = new KeyBinding("key.refinedstorage.focusSearchBar", KeyConflictContext.GUI, Keyboard.KEY_TAB, "Refined Storage");

    public static void init() {
        ClientRegistry.registerKeyBinding(FOCUS_SEARCH_BAR);
    }
}