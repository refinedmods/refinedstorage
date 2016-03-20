package refinedstorage.gui;

import refinedstorage.container.ContainerInterface;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileInterface;

public class GuiInterface extends GuiBase
{
	private TileInterface tile;

	public GuiInterface(ContainerInterface container, TileInterface tile)
	{
		super(container, 176, 218);

		this.tile = tile;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(tile));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/interface.png");

		drawTexture(x, y, 0, 0, width, height);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.refinedstorage:interface.import"));
		drawString(7, 42, t("gui.refinedstorage:interface.export"));
		drawString(7, 123, t("container.inventory"));
	}
}
