package refinedstorage.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.storage.CellStorage;
import refinedstorage.tile.TileStorage;

@JEIPlugin
public class PluginRefinedStorage extends BlankModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		// @TODO: JEI transfer handler
		registry.addRecipeCategories(new SoldererRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

		registry.addRecipeHandlers(new SoldererRecipeHandler());

		registry.addRecipes(SoldererRecipeMaker.getRecipes());

		registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(RefinedStorageItems.STORAGE_CELL, CellStorage.NBT_ITEMS, CellStorage.NBT_STORED);
		registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), TileStorage.NBT_STORAGE);
	}
}
