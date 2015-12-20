package storagecraft.gui;

import storagecraft.container.ContainerDrive;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileDrive;

public class GuiDrive extends GuiBase {
	private TileDrive drive;

	public GuiDrive(ContainerDrive container, TileDrive drive) {
		super(container, 176, 190);

		this.drive = drive;
	}

	@Override
	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(drive));
	}

	@Override
	public void update(int x, int y) {
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/drive.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.storagecraft:drive"));
		drawString(7, 96, t("container.inventory"));
	}
}
