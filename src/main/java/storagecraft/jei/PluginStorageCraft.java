package storagecraft.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class PluginStorageCraft implements IModPlugin
{
	private IItemRegistry itemRegistry;
	private IJeiHelpers jeiHelpers;

	@Override
	public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers)
	{
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public void onItemRegistryAvailable(IItemRegistry itemRegistry)
	{
		this.itemRegistry = itemRegistry;
	}

	@Override
	public void register(IModRegistry registry)
	{
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SoldererRecipeCategory(guiHelper));

		registry.addRecipeHandlers(new SoldererRecipeHandler());

		registry.addRecipes(SoldererRecipeMaker.getRecipes());
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry)
	{
	}
}
