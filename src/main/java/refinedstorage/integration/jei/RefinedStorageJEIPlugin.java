package refinedstorage.integration.jei;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;

@JEIPlugin
public class RefinedStorageJEIPlugin extends BlankModPlugin {
    public static RefinedStorageJEIPlugin INSTANCE;

    private IJeiRuntime runtime;

    @Override
    public void register(IModRegistry registry) {
        INSTANCE = this;

        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RecipeTransferHandlerGrid());

        registry.addRecipeCategories(new RecipeCategorySolderer(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new RecipeHandlerSolderer());

        registry.addRecipes(RecipeMakerSolderer.getRecipes());

        registry.addRecipeCategoryCraftingItem(new ItemStack(RefinedStorageBlocks.SOLDERER), RecipeCategorySolderer.ID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }
}
