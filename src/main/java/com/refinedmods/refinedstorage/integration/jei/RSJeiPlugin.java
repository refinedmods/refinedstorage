package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class RSJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(RS.ID, "plugin");

    private static IJeiRuntime runtime;

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(GridRecipeTransferHandler.INSTANCE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        JeiHelper jeiHelper = new JeiHelper(registration.getJeiHelpers().getIngredientManager());
        registration.addGenericGuiContainerHandler(BaseScreen.class, new GuiContainerHandler(jeiHelper));
        registration.addGhostIngredientHandler(BaseScreen.class, new GhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RSJeiPlugin.runtime = runtime;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(RSItems.COVER.get(), RSItems.HOLLOW_COVER.get());
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(CoverRecipe.class, cover -> new CoverCraftingCategoryExtension());
        registration.getCraftingCategory().addCategoryExtension(HollowCoverRecipe.class, cover -> new HollowCoverCraftingCategoryExtension());
    }
}
