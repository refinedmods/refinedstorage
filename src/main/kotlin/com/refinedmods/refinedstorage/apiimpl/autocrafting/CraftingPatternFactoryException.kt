package com.refinedmods.refinedstorage.apiimpl.autocrafting

import net.minecraft.util.text.Text


class CraftingPatternFactoryException(errorMessage: Text) : Exception() {
    private val errorMessage: Text
    fun getErrorMessage(): Text {
        return errorMessage
    }

    init {
        this.errorMessage = errorMessage
    }
}