package storagecraft.gui;

import com.google.common.primitives.Ints;
import java.io.IOException;
import net.minecraft.client.gui.GuiTextField;
import storagecraft.container.ContainerStorage;
import storagecraft.gui.sidebutton.SideButtonCompare;
import storagecraft.gui.sidebutton.SideButtonMode;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.storage.IStorageGui;
import storagecraft.util.InventoryUtils;

public class GuiStorage extends GuiBase
{
	private IStorageGui gui;

	private GuiTextField priorityField;

	private int barX = 8;
	private int barY = 54;
	private int barWidth = 16;
	private int barHeight = 58;

	public GuiStorage(ContainerStorage container, IStorageGui gui)
	{
		super(container, 176, 211);

		this.gui = gui;
	}

	@Override
	public void init(int x, int y)
	{
		if (gui.getRedstoneModeSetting() != null)
		{
			addSideButton(new SideButtonRedstoneMode(gui.getRedstoneModeSetting()));
		}

		if (gui.getWhitelistBlacklistSetting() != null)
		{
			addSideButton(new SideButtonMode(gui.getWhitelistBlacklistSetting()));
		}

		if (gui.getCompareSetting() != null)
		{
			addSideButton(new SideButtonCompare(gui.getCompareSetting(), InventoryUtils.COMPARE_DAMAGE));
			addSideButton(new SideButtonCompare(gui.getCompareSetting(), InventoryUtils.COMPARE_NBT));
		}

		priorityField = new GuiTextField(0, fontRendererObj, x + 116 + 1, y + 54 + 1, 25, fontRendererObj.FONT_HEIGHT);
		priorityField.setText(String.valueOf(gui.getStorage().getPriority()));
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

		drawTexture(x, y, 0, 0, width, height);

		int barHeightNew = (int) ((float) gui.getStored() / (float) gui.getCapacity() * (float) barHeight);

		drawTexture(x + barX, y + barY + barHeight - barHeightNew, 179, 0 + (barHeight - barHeightNew), barWidth, barHeightNew);

		priorityField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t(gui.getName()));
		drawString(7, 42, t("misc.storagecraft:storage"));
		drawString(115, 42, t("misc.storagecraft:priority"));
		drawString(7, 117, t("container.inventory"));

		drawString(30, 54, t("misc.storagecraft:storage.stored", gui.getStored()));

		if (gui.getCapacity() != -1)
		{
			drawString(30, 64, t("misc.storagecraft:storage.capacity", gui.getCapacity()));
		}

		if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY))
		{
			drawTooltip(mouseX, mouseY, t("misc.storagecraft:storage.full", (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f)));
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
				gui.getPriorityHandler().onPriorityChanged(result);
			}
		}
		else
		{
			super.keyTyped(character, keyCode);
		}
	}
}
