package com.refinedmods.refinedstorage.apiimpl.util;

import net.minecraft.util.registry.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class MinecraftTest {
    @BeforeAll
    static void register() {
        Bootstrap.register();
    }
}
