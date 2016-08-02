package refinedstorage.integration.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import refinedstorage.RefinedStorageBlocks;

@JEIPlugin
public class JEIIntegration implements IModPlugin {
    public static JEIIntegration INSTANCE;

    private IJeiRuntime runtime;

    @Override
    public void register(IModRegistry registry) {
        INSTANCE = this;

        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RecipeTransferHandlerGrid());

        registry.addRecipeCategories(new SoldererRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new SoldererRecipeHandler());

        registry.addRecipes(SoldererRecipeMaker.getRecipes());

        registry.addRecipeCategoryCraftingItem(new ItemStack(RefinedStorageBlocks.SOLDERER), SoldererRecipeCategory.ID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }

    public static boolean isLoaded() {
        return Loader.isModLoaded("JEI");
    }
}
