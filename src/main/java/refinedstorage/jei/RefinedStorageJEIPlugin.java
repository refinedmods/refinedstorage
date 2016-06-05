package refinedstorage.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.storage.NBTStorage;
import refinedstorage.tile.TileStorage;

@JEIPlugin
public class RefinedStorageJEIPlugin implements IModPlugin {
    public static RefinedStorageJEIPlugin INSTANCE;

    private IJeiRuntime runtime;

    @Override
    public void register(IModRegistry registry) {
        INSTANCE = this;

        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new GridRecipeTransferHandler());

        registry.addRecipeCategories(new SoldererRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new SoldererRecipeHandler());

        registry.addRecipes(SoldererRecipeMaker.getRecipes());

        registry.addRecipeCategoryCraftingItem(new ItemStack(RefinedStorageBlocks.SOLDERER), SoldererRecipeCategory.ID);

        registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(RefinedStorageItems.STORAGE_DISK, NBTStorage.NBT_ITEMS, NBTStorage.NBT_STORED);
        registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), TileStorage.NBT_STORAGE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }
}
