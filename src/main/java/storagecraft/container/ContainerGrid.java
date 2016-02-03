package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftItems;
import storagecraft.block.EnumGridType;
import storagecraft.container.slot.SlotDisabled;
import storagecraft.container.slot.SlotFiltered;
import storagecraft.container.slot.SlotGridCraftingResult;
import storagecraft.container.slot.SlotOutput;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileGrid;

public class ContainerGrid extends ContainerBase
{
	private TileGrid grid;

	public ContainerGrid(EntityPlayer player, TileGrid grid)
	{
		super(player);

		this.grid = grid;

		addPlayerInventory(8, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 174 : 108);

		if (grid.getType() == EnumGridType.CRAFTING)
		{
			int x = 25;
			int y = 106;

			for (int i = 0; i < 9; ++i)
			{
				addSlotToContainer(new Slot(grid.getCraftingInventory(), i, x, y));

				x += 18;

				if ((i + 1) % 3 == 0)
				{
					y += 18;
					x = 25;
				}
			}

			addSlotToContainer(new SlotGridCraftingResult(player, grid.getCraftingInventory(), grid.getCraftingResultInventory(), grid, 0, 133 + 4, 120 + 4));
		}
		else if (grid.getType() == EnumGridType.PATTERN)
		{
			int x = 25;
			int y = 106;

			for (int i = 0; i < 9; ++i)
			{
				addSlotToContainer(new SlotSpecimen(grid.getPatternCraftingInventory(), i, x, y));

				x += 18;

				if ((i + 1) % 3 == 0)
				{
					y += 18;
					x = 25;
				}
			}

			addSlotToContainer(new SlotDisabled(grid.getPatternCraftingResultInventory(), 0, 133 + 4, 120 + 4));

			addSlotToContainer(new SlotFiltered(grid.getPatternInventory(), 0, 137, 98, StorageCraftItems.PATTERN));
			addSlotToContainer(new SlotOutput(grid.getPatternInventory(), 1, 137, 150));
		}
	}

	@Override
	public ItemStack slotClick(int id, int clickedButton, int mode, EntityPlayer player)
	{
		if (id >= 0 && getSlot(id) instanceof SlotDisabled)
		{
			grid.onPatternCreate();
		}

		return super.slotClick(id, clickedButton, mode, player);
	}
}
