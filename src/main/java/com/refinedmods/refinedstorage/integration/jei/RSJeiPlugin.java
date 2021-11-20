package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

@JeiPlugin
public class RSJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(RS.ID, "plugin");

    private static IJeiRuntime runtime;

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
        registration.addGenericGuiContainerHandler(BaseScreen.class, new GuiContainerHandler());
        registration.addGhostIngredientHandler(BaseScreen.class, new GhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RSJeiPlugin.runtime = runtime;
    }

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(RSItems.COVER.get(), RSItems.HOLLOW_COVER.get());
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(CoverRecipe.class, (cover) -> new CoverCraftingCategoryExtension());
        registration.getCraftingCategory().addCategoryExtension(HollowCoverRecipe.class, (cover) -> new HollowCoverCraftingCategoryExtension());
    }
}
