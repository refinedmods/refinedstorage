package storagecraft.gui;

import storagecraft.container.ContainerStorageProxy;
import storagecraft.tile.TileStorageProxy;

public class GuiStorageProxy extends GuiMachine {
	public GuiStorageProxy(ContainerStorageProxy container, TileStorageProxy storageProxy) {
		super(container, 176, 131, storageProxy);
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/storageProxy.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:storageProxy"));
		drawString(7, 39, t("container.inventory"));
	}
}
