package storagecraft.gui;

import net.minecraft.inventory.Container;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileWirelessTransmitter;

public class GuiWirelessTransmitter extends GuiBase
{
	private TileWirelessTransmitter wirelessTransmitter;

	public GuiWirelessTransmitter(Container container, TileWirelessTransmitter wirelessTransmitter)
	{
		super(container, 176, 137);

		this.wirelessTransmitter = wirelessTransmitter;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(wirelessTransmitter));
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/wireless_transmitter.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		if (wirelessTransmitter.isWorking())
		{
			int progress = (int) ((float) wirelessTransmitter.getProgress() / (float) TileWirelessTransmitter.TOTAL_PROGRESS * 14f);

			drawTexturedModalRect(x + 36 - 1, y + 21 - 1 + progress, 178, 0 + progress, 14, 14);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:wireless_transmitter"));
		drawString(7, 43, t("container.inventory"));
	}
}
