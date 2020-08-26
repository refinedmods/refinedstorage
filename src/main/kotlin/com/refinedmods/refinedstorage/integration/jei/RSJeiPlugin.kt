package com.refinedmods.refinedstorage.integration.jei

import com.refinedmods.refinedstorage.RS
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeTransferRegistration
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.util.Identifier

@JeiPlugin
class RSJeiPlugin : IModPlugin {
    val pluginUid: Identifier
        get() = ID

    fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addUniversalRecipeTransferHandler(GridRecipeTransferHandler())
    }

    fun registerGuiHandlers(registration: IGuiHandlerRegistration?) {
        // TODO registration.addGuiContainerHandler(BaseScreen.class, new GuiContainerHandler());

        // TODO: https://github.com/mezz/JustEnoughItems/issues/1307
        // registration.addGhostIngredientHandler(BaseScreen.class, new GhostIngredientHandler());
    }

    fun onRuntimeAvailable(runtime: IJeiRuntime?) {
        RUNTIME = runtime
    }

    companion object {
        private val ID: Identifier = Identifier(RS.ID, "plugin")
        @JvmField
        var RUNTIME: IJeiRuntime? = null
    }
}