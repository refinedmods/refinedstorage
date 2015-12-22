package storagecraft.gui;

import storagecraft.container.ContainerController;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileController;

public class GuiController extends GuiBase
{
	private TileController controller;

	public GuiController(ContainerController container, TileController controller)
	{
		super(container, 176, 190);

		this.controller = controller;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(controller));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/controller.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int barX = 17;
		int barY = 25;
		int barWidth = 16;
		int barHeight = 58;

		int barHeightNew = (int) ((float) controller.getEnergyStored(null) / (float) controller.getMaxEnergyStored(null) * (float) barHeight);

		drawTexturedModalRect(x + barX, y + barY + barHeight - barHeightNew, 178, 0, barWidth, barHeightNew);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:controller"));
		drawString(7, 96, t("container.inventory"));

		drawString(45, 24, t("misc.storagecraft:energyStored", controller.getEnergyStored(null), controller.getMaxEnergyStored(null)));
		drawString(45, 44, t("misc.storagecraft:energyUsage", controller.getEnergyUsage()));
	}
}
