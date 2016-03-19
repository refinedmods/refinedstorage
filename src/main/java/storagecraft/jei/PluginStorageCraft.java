package storagecraft.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.storage.CellStorage;
import storagecraft.tile.TileStorage;

@JEIPlugin
public class PluginStorageCraft extends BlankModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addRecipeCategories(new SoldererRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

		registry.addRecipeHandlers(new SoldererRecipeHandler());

		registry.addRecipes(SoldererRecipeMaker.getRecipes());

		registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(StorageCraftItems.STORAGE_CELL, CellStorage.NBT_ITEMS, CellStorage.NBT_STORED);
		registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(Item.getItemFromBlock(StorageCraftBlocks.STORAGE), TileStorage.NBT_STORAGE);
	}
}
