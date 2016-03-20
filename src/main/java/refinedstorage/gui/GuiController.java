package refinedstorage.gui;

import refinedstorage.container.ContainerController;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileController;

public class GuiController extends GuiBase
{
	private TileController controller;

	private int barX = 8;
	private int barY = 20;
	private int barWidth = 16;
	private int barHeight = 58;

	public GuiController(ContainerController container, TileController controller)
	{
		super(container, 176, 181);

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

		drawTexture(x, y, 0, 0, width, height);

		int barHeightNew = (int) ((float) controller.getEnergyStored(null) / (float) controller.getMaxEnergyStored(null) * (float) barHeight);

		drawTexture(x + barX, y + barY + barHeight - barHeightNew, 178, 0 + (barHeight - barHeightNew), barWidth, barHeightNew);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.refinedstorage:controller." + controller.getType().getId()));
		drawString(7, 87, t("container.inventory"));

		drawString(31, 20, t("misc.refinedstorage:energy_usage", controller.getEnergyUsage()));

		if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY))
		{
			drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_stored", controller.getEnergyStored(null), controller.getMaxEnergyStored(null)));
		}
	}
}
