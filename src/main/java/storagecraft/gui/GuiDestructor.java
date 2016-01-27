package storagecraft.gui;

import storagecraft.container.ContainerDestructor;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileDestructor;

public class GuiDestructor extends GuiBase
{
	private TileDestructor destructor;

	public GuiDestructor(ContainerDestructor container, TileDestructor destructor)
	{
		super(container, 176, 131);

		this.destructor = destructor;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(destructor));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/destructor.png");

		drawTexture(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:destructor"));
		drawString(7, 39, t("container.inventory"));
	}
}
