package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageGridSortingUpdate;
import refinedstorage.tile.TileGrid;

public class SideButtonGridSortingDirection extends SideButton
{
	private TileGrid grid;

	public SideButtonGridSortingDirection(TileGrid grid)
	{
		this.grid = grid;
	}

	@Override
	public String getTooltip(GuiBase gui)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(TextFormatting.YELLOW).append(gui.t("sidebutton.refinedstorage:sorting.direction")).append(TextFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.refinedstorage:sorting.direction." + grid.getSortingDirection()));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y)
	{
		gui.bindTexture("icons.png");
		gui.drawTexture(x, y + 2 - 1, grid.getSortingDirection() * 16, 16, 16, 16);
	}

	@Override
	public void actionPerformed()
	{
		int dir = grid.getSortingDirection();

		if (dir == TileGrid.SORTING_DIRECTION_ASCENDING)
		{
			dir = TileGrid.SORTING_DIRECTION_DESCENDING;
		}
		else if (dir == TileGrid.SORTING_DIRECTION_DESCENDING)
		{
			dir = TileGrid.SORTING_DIRECTION_ASCENDING;
		}

		RefinedStorage.NETWORK.sendToServer(new MessageGridSortingUpdate(grid, dir, grid.getSortingType()));
	}
}
