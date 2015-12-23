package storagecraft.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import storagecraft.StorageCraft;

public class NEIConfig implements IConfigureNEI
{
	@Override
	public void loadConfig()
	{
		API.registerRecipeHandler(new CraftingHandlerSolderer());
		API.registerUsageHandler(new CraftingHandlerSolderer());
		// API.setGuiOffset(GuiSolderer.class, 0, 0);
	}

	@Override
	public String getName()
	{
		return "StorageCraft Plugin";
	}

	@Override
	public String getVersion()
	{
		return StorageCraft.VERSION;
	}
}
