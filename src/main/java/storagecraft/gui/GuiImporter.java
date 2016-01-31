package storagecraft.gui;

import storagecraft.container.ContainerImporter;
import storagecraft.gui.sidebutton.SideButtonCompare;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.gui.sidebutton.SideButtonMode;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiBase
{
	private TileImporter importer;

	public GuiImporter(ContainerImporter container, TileImporter importer)
	{
		super(container, 176, 137);

		this.importer = importer;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(importer));

		addSideButton(new SideButtonMode(importer));

		addSideButton(new SideButtonCompare(importer, InventoryUtils.COMPARE_DAMAGE));
		addSideButton(new SideButtonCompare(importer, InventoryUtils.COMPARE_NBT));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/importer.png");

		drawTexture(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:importer"));
		drawString(7, 43, t("container.inventory"));
	}
}
