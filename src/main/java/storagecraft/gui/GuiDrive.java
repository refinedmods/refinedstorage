package storagecraft.gui;

import storagecraft.container.ContainerDrive;
import storagecraft.tile.TileDrive;

public class GuiDrive extends GuiMachine {
	public GuiDrive(ContainerDrive container, TileDrive drive) {
		super(container, 176, 190, drive);
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/drive.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:drive"));
		drawString(7, 96, t("container.inventory"));
	}
}
