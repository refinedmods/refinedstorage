package storagecraft.gui;

import com.google.common.primitives.Ints;
import java.io.IOException;
import net.minecraft.client.gui.GuiTextField;
import storagecraft.StorageCraft;
import storagecraft.block.EnumStorageType;
import storagecraft.container.ContainerStorage;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.network.MessageStoragePriorityUpdate;
import storagecraft.tile.TileStorage;

public class GuiStorage extends GuiBase
{
	private TileStorage storage;

	private GuiTextField priorityField;

	private int barX = 8;
	private int barY = 54;
	private int barWidth = 16;
	private int barHeight = 58;

	public GuiStorage(ContainerStorage container, TileStorage storage)
	{
		super(container, 176, 211);

		this.storage = storage;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(storage));

		priorityField = new GuiTextField(0, fontRendererObj, x + 116 + 1, y + 54 + 1, 25, fontRendererObj.FONT_HEIGHT);
		priorityField.setText(String.valueOf(storage.getPriority()));
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
		bindTexture("gui/storage.png");

		drawTexture(x, y, 0, 0, xSize, ySize);

		int barHeightNew = storage.getStoredScaled(barHeight);

		drawTexture(x + barX, y + barY + barHeight - barHeightNew, 179, 0 + (barHeight - barHeightNew), barWidth, barHeightNew);

		priorityField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("block.storagecraft:storage." + storage.getType().getId() + ".name"));
		drawString(7, 42, t("misc.storagecraft:storage"));
		drawString(115, 42, t("misc.storagecraft:priority"));
		drawString(7, 117, t("container.inventory"));

		drawString(30, 54, t("misc.storagecraft:storage.stored", storage.getStored()));

		if (storage.getType() != EnumStorageType.TYPE_CREATIVE)
		{
			drawString(30, 64, t("misc.storagecraft:storage.capacity", storage.getType().getCapacity()));
		}

		if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY))
		{
			drawTooltip(mouseX, mouseY, t("misc.storagecraft:storage.full", storage.getStoredScaled(100)));
		}
	}

	@Override
	protected void keyTyped(char character, int keyCode) throws IOException
	{
		if (!checkHotbarKeys(keyCode) && priorityField.textboxKeyTyped(character, keyCode))
		{
			Integer result = Ints.tryParse(priorityField.getText());

			if (result != null)
			{
				StorageCraft.NETWORK.sendToServer(new MessageStoragePriorityUpdate(storage, result));
			}
		}
		else
		{
			super.keyTyped(character, keyCode);
		}
	}
}
