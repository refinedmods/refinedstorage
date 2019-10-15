package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class RSJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(RS.ID, "plugin");

    public static IJeiRuntime RUNTIME;

    // TODO registry.addRecipeRegistryPlugin(new RecipeRegistryPluginCover());
    // TODO registry.addRecipeRegistryPlugin(new RecipeRegistryPluginHollowCover());

    // TODO: https://github.com/mezz/JustEnoughItems/issues/1307
    // registry.addGhostIngredientHandler(GuiBase.class, new GhostIngredientHandler());

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new GridRecipeTransferHandler());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BaseScreen.class, new GuiContainerHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RUNTIME = runtime;
    }
}
