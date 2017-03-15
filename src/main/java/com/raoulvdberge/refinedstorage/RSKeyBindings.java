package com.raoulvdberge.refinedstorage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class RSKeyBindings {
    private static final String categoryName = RS.ID;

    public static final KeyBinding focusSearchBar = new KeyBinding("key.rs.focusSearchBar", KeyConflictContext.GUI, Keyboard.KEY_TAB, categoryName);

    public static void init() {
        ClientRegistry.registerKeyBinding(focusSearchBar);
    }
}
