package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import storagecraft.container.slot.SlotGridCraftingResult;
import storagecraft.tile.TileGrid;

public class ContainerGrid extends ContainerBase
{
	public ContainerGrid(EntityPlayer player, TileGrid grid)
	{
		super(player);

		addPlayerInventory(8, grid.isCrafting() ? 174 : 108);

		if (grid.isCrafting())
		{
			int x = 44;
			int y = 106;

			for (int i = 0; i < 9; ++i)
			{
				addSlotToContainer(new Slot(grid.getCraftingMatrix(), i, x, y));

				x += 18;

				if ((i + 1) % 3 == 0)
				{
					y += 18;
					x = 44;
				}
			}

			addSlotToContainer(new SlotGridCraftingResult(player, grid.getCraftingMatrix(), grid.getCraftingResult(), grid, 0, 125, 124));
		}
	}
}
