package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.gui.GuiBase;
import storagecraft.gui.GuiGrid;

public class SideButtonGridSortingType extends SideButton
{
	@Override
	public String getTooltip(GuiBase gui)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.YELLOW).append(gui.t("sidebutton.storagecraft:sorting.type")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:sorting.type." + GuiGrid.SORTING_TYPE));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y)
	{
		gui.bindTexture("icons.png");
		gui.drawTexturedModalRect(x, y + 2, GuiGrid.SORTING_TYPE * 16, 32, 16, 16);
	}

	@Override
	public void actionPerformed()
	{
		GuiGrid.SORTING_TYPE = GuiGrid.SORTING_TYPE == GuiGrid.SORTING_TYPE_COUNT ? GuiGrid.SORTING_TYPE_NAME : GuiGrid.SORTING_TYPE_COUNT;
	}
}
