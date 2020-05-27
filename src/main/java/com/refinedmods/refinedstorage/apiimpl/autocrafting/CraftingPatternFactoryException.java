package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import net.minecraft.util.text.ITextComponent;

public class CraftingPatternFactoryException extends Exception {
    private final ITextComponent errorMessage;

    public CraftingPatternFactoryException(ITextComponent errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ITextComponent getErrorMessage() {
        return errorMessage;
    }
}
