package storagecraft.tile.crafting;

import java.util.List;
import net.minecraft.item.ItemStack;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileController;

public class CraftingTask
{
	private ItemStack result;
	private ItemStack[] requirements;
	private List<CraftingTask> subTasks;

	private TileController controller;

	public CraftingTask(TileController controller)
	{
		this.controller = controller;
	}

	public boolean craft()
	{
		for (ItemStack requirement : requirements)
		{
			boolean found = false;

			for (StorageItem item : controller.getItems())
			{
				if (item.compareNoQuantity(requirement))
				{
					found = true;

					controller.take(requirement);

					break;
				}
			}

			if (!found)
			{
				// now look for a crafter for requirement and craft it.
				// if not found, return false.
			}
		}

		return true;
	}
}
