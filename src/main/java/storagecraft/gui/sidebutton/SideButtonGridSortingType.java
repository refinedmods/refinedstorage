package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageGridSortingUpdate;
import storagecraft.tile.TileGrid;

public class SideButtonGridSortingType extends SideButton
{
	private TileGrid grid;

	public SideButtonGridSortingType(TileGrid grid)
	{
		this.grid = grid;
	}

	@Override
	public String getTooltip(GuiBase gui)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.YELLOW).append(gui.t("sidebutton.storagecraft:sorting.type")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:sorting.type." + grid.getSortingType()));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y)
	{
		gui.bindTexture("icons.png");
		gui.drawTexture(x, y + 2 - 1, grid.getSortingType() * 16, 32, 16, 16);
	}

	@Override
	public void actionPerformed()
	{
		int type = grid.getSortingType();

		if (type == TileGrid.SORTING_TYPE_QUANTITY)
		{
			type = TileGrid.SORTING_TYPE_NAME;
		}
		else if (type == TileGrid.SORTING_TYPE_NAME)
		{
			type = TileGrid.SORTING_TYPE_QUANTITY;
		}

		StorageCraft.NETWORK.sendToServer(new MessageGridSortingUpdate(grid, grid.getSortingDirection(), type));
	}
}
