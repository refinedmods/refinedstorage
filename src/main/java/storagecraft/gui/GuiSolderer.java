package storagecraft.gui;

import net.minecraft.inventory.Container;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileSolderer;

public class GuiSolderer extends GuiBase
{
	private TileSolderer solderer;

	public GuiSolderer(Container container, TileSolderer solderer)
	{
		super(container, 176, 177);

		this.solderer = solderer;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(solderer));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/solderer.png");

		drawTexture(x, y, 0, 0, xSize, ySize);

		if (solderer.isWorking())
		{
			drawTexture(x + 83, y + 40 - 1, 177, 0, solderer.getProgressScaled(22), 15);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:solderer"));
		drawString(7, 82, t("container.inventory"));
	}
}
