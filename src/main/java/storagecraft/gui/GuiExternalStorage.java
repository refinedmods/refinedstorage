package storagecraft.gui;

import storagecraft.container.ContainerExternalStorage;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileExternalStorage;

public class GuiExternalStorage extends GuiBase
{
	private TileExternalStorage externalStorage;

	public GuiExternalStorage(ContainerExternalStorage container, TileExternalStorage externalStorage)
	{
		super(container, 176, 131);

		this.externalStorage = externalStorage;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(externalStorage));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/external_storage.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:external_storage"));
		drawString(7, 39, t("container.inventory"));
	}
}
