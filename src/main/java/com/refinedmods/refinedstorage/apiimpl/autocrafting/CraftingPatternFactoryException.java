package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import net.minecraft.network.chat.Component;

public class CraftingPatternFactoryException extends Exception {
    private final transient Component errorMessage;

    public CraftingPatternFactoryException(Component errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Component getErrorMessage() {
        return errorMessage;
    }
}
