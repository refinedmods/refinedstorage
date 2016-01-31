package storagecraft.gui;

import com.google.common.primitives.Ints;
import java.io.IOException;
import net.minecraft.client.gui.GuiTextField;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerDrive;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.network.MessagePriorityUpdate;
import storagecraft.tile.TileDrive;

public class GuiDrive extends GuiBase
{
	private TileDrive drive;

	private GuiTextField priorityField;

	public GuiDrive(ContainerDrive container, TileDrive drive)
	{
		super(container, 176, 190);

		this.drive = drive;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(drive));

		priorityField = new GuiTextField(0, fontRendererObj, x + 52 + 1, y + 32 + 1, 25, fontRendererObj.FONT_HEIGHT);
		priorityField.setText(String.valueOf(drive.getPriority()));
		priorityField.setEnableBackgroundDrawing(false);
		priorityField.setVisible(true);
		priorityField.setTextColor(16777215);
		priorityField.setCanLoseFocus(false);
		priorityField.setFocused(true);
	}

	@Override
	public void update(int x, int y)
	{
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		bindTexture("gui/drive.png");

		drawTexture(x, y, 0, 0, width, height);

		priorityField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:drive"));
		drawString(51, 20, t("misc.storagecraft:priority"));
		drawString(7, 96, t("container.inventory"));
	}

	@Override
	protected void keyTyped(char character, int keyCode) throws IOException
	{
		if (!checkHotbarKeys(keyCode) && priorityField.textboxKeyTyped(character, keyCode))
		{
			Integer result = Ints.tryParse(priorityField.getText());

			if (result != null)
			{
				StorageCraft.NETWORK.sendToServer(new MessagePriorityUpdate(drive.getPos(), result));
			}
		}
		else
		{
			super.keyTyped(character, keyCode);
		}
	}
}
