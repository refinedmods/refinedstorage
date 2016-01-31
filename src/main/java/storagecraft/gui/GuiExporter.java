package storagecraft.gui;

import storagecraft.container.ContainerExporter;
import storagecraft.gui.sidebutton.SideButtonCompare;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileExporter;
import storagecraft.util.InventoryUtils;

public class GuiExporter extends GuiBase
{
	private TileExporter exporter;

	public GuiExporter(ContainerExporter container, TileExporter exporter)
	{
		super(container, 176, 137);

		this.exporter = exporter;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(exporter));

		addSideButton(new SideButtonCompare(exporter, InventoryUtils.COMPARE_DAMAGE));
		addSideButton(new SideButtonCompare(exporter, InventoryUtils.COMPARE_NBT));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/exporter.png");

		drawTexture(x, y, 0, 0, width, height);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:exporter"));
		drawString(7, 43, t("container.inventory"));
	}
}
