package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.gui.GuiBase;
import storagecraft.gui.GuiGrid;

public class SideButtonGridSortingDirection extends SideButton {
	@Override
	public String getTooltip(GuiBase gui) {
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.YELLOW).append(gui.t("sidebutton.storagecraft:sorting.direction")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:sorting.direction." + GuiGrid.SORTING_DIRECTION));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y) {
	}

	@Override
	public void actionPerformed() {
		GuiGrid.SORTING_DIRECTION = GuiGrid.SORTING_DIRECTION == GuiGrid.SORTING_DIRECTION_ASCENDING ? GuiGrid.SORTING_DIRECTION_DESCENDING : GuiGrid.SORTING_DIRECTION_ASCENDING;
	}
}
