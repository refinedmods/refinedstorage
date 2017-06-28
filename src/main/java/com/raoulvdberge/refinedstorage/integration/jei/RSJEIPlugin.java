package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.container.ContainerSolderer;
import com.raoulvdberge.refinedstorage.gui.GuiSolderer;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class RSJEIPlugin implements IModPlugin {
    public static RSJEIPlugin INSTANCE;

    private IJeiRuntime runtime;

    @Override
    public void register(IModRegistry registry) {
        INSTANCE = this;

        registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RecipeTransferHandlerGrid());
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSolderer.class, RecipeCategorySolderer.ID, 0, 3, 8, 36);

        registry.handleRecipes(RecipeWrapperSolderer.class, recipe -> recipe, RecipeCategorySolderer.ID);

        registry.addRecipes(RecipeMakerSolderer.getRecipes(registry.getJeiHelpers().getGuiHelper()), RecipeCategorySolderer.ID);

        registry.addRecipeCatalyst(new ItemStack(RSBlocks.SOLDERER), RecipeCategorySolderer.ID);

        registry.addAdvancedGuiHandlers(new GuiHandlerGrid());

        registry.addRecipeClickArea(GuiSolderer.class, 80, 36, 22, 15, RecipeCategorySolderer.ID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new RecipeCategorySolderer(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }
}
