package com.raoulvdberge.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class RSKeyBindings {
    private static final String CATEGORY_NAME = RS.ID;

    public static final KeyBinding BINDING_FOCUS_SEARCH_BAR = new KeyBinding("key.rs.focusSearchBar", KeyConflictContext.GUI, Keyboard.KEY_TAB, CATEGORY_NAME);

    public static void init() {
        ClientRegistry.registerKeyBinding(BINDING_FOCUS_SEARCH_BAR);
    }
}
