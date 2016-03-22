package refinedstorage.gui;

import com.google.common.primitives.Ints;
import java.io.IOException;
import net.minecraft.client.gui.GuiTextField;
import refinedstorage.container.ContainerStorage;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.storage.IStorageGui;
import refinedstorage.util.InventoryUtils;

public class GuiStorage extends GuiBase
{
	private IStorageGui gui;
	private String texture;

	private GuiTextField priorityField;

	private int barX = 8;
	private int barY = 54;
	private int barWidth = 16;
	private int barHeight = 58;

	public GuiStorage(ContainerStorage container, IStorageGui gui, String texture)
	{
		super(container, 176, 211);

		this.gui = gui;
		this.texture = texture;
	}

	public GuiStorage(ContainerStorage container, IStorageGui gui)
	{
		this(container, gui, "gui/storage.png");
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

		priorityField = new GuiTextField(0, fontRendererObj, x + 98 + 1, y + 54 + 1, 25, fontRendererObj.FONT_HEIGHT);
		priorityField.setText(String.valueOf(gui.getPriority()));
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
		bindTexture(texture);

		drawTexture(x, y, 0, 0, width, height);

		int barHeightNew = (int) ((float) gui.getStored() / (float) gui.getCapacity() * (float) barHeight);

		drawTexture(x + barX, y + barY + barHeight - barHeightNew, 179, 0 + (barHeight - barHeightNew), barWidth, barHeightNew);

		priorityField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t(gui.getName()));
		drawString(7, 42, t("misc.refinedstorage:storage"));
		drawString(97, 42, t("misc.refinedstorage:priority"));
		drawString(7, 117, t("container.inventory"));

		drawString(30, 54, t("misc.refinedstorage:storage.stored", gui.getStored()));

		if (gui.getCapacity() != -1)
		{
			drawString(30, 64, t("misc.refinedstorage:storage.capacity", gui.getCapacity()));
		}

		if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY))
		{
			int full = 0;

			if (gui.getCapacity() >= 0)
			{
				full = (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f);
			}

			drawTooltip(mouseX, mouseY, t("misc.refinedstorage:storage.full", full));
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
