package storagecraft.gui;

import storagecraft.container.ContainerStorageProxy;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileStorageProxy;

public class GuiStorageProxy extends GuiBase
{
	private TileStorageProxy storageProxy;

	public GuiStorageProxy(ContainerStorageProxy container, TileStorageProxy storageProxy)
	{
		super(container, 176, 131);

		this.storageProxy = storageProxy;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(storageProxy));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/storageProxy.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:storageProxy"));
		drawString(7, 39, t("container.inventory"));
	}
}
