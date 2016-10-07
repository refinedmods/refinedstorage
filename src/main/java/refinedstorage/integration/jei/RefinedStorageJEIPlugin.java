package refinedstorage.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import refinedstorage.RSBlocks;
import refinedstorage.container.ContainerSolderer;

@JEIPlugin
public class RefinedStorageJEIPlugin extends BlankModPlugin {
    public static RefinedStorageJEIPlugin INSTANCE;

    private IJeiRuntime runtime;

    @Override
    public void register(IModRegistry registry) {
        INSTANCE = this;

        registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RecipeTransferHandlerPattern());
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RecipeTransferHandlerGrid(), "minecraft.crafting");
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSolderer.class, RecipeCategorySolderer.ID, 0, 3, 8, 36);

        registry.addRecipeCategories(new RecipeCategorySolderer(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new RecipeHandlerSolderer());

        registry.addRecipes(RecipeMakerSolderer.getRecipes());

        registry.addRecipeCategoryCraftingItem(new ItemStack(RSBlocks.SOLDERER), RecipeCategorySolderer.ID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }
}
