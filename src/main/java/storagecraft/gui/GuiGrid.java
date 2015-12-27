package storagecraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import storagecraft.StorageCraft;
import storagecraft.block.EnumGridType;
import storagecraft.container.ContainerGrid;
import storagecraft.gui.sidebutton.SideButtonGridSortingDirection;
import storagecraft.gui.sidebutton.SideButtonGridSortingType;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.network.MessageGridCraftingClear;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiBase
{
	public static final int SORTING_DIRECTION_ASCENDING = 0;
	public static final int SORTING_DIRECTION_DESCENDING = 1;

	public static final int SORTING_TYPE_COUNT = 0;
	public static final int SORTING_TYPE_NAME = 1;

	public static int SORTING_DIRECTION = SORTING_DIRECTION_ASCENDING;
	public static int SORTING_TYPE = SORTING_TYPE_COUNT;

	private ContainerGrid container;
	private TileGrid grid;

	private GuiTextField searchField;

	private int hoveringSlotId;
	private int hoveringId;

	private int offset;

	public GuiGrid(ContainerGrid container, TileGrid grid)
	{
		super(container, 176, grid.getType() == EnumGridType.CRAFTING ? 256 : 190);

		this.container = container;
		this.grid = grid;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(grid));

		addSideButton(new SideButtonGridSortingDirection());
		addSideButton(new SideButtonGridSortingType());

		searchField = new GuiTextField(0, fontRendererObj, x + 80 + 1, y + 6 + 1, 88 - 6, fontRendererObj.FONT_HEIGHT);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setVisible(true);
		searchField.setTextColor(16777215);
		searchField.setCanLoseFocus(false);
		searchField.setFocused(true);
	}

	@Override
	public void update(int x, int y)
	{
		int wheel = Mouse.getDWheel();

		wheel = Math.max(Math.min(-wheel, 1), -1);

		if (canScroll(wheel))
		{
			offset += wheel;
		}

		if (offset > getMaxOffset())
		{
			offset = getMaxOffset();
		}
	}

	private int getMaxOffset()
	{
		if (!grid.isConnected())
		{
			return 0;
		}

		int max = ((int) Math.ceil((float) getItems().size() / (float) 9)) - 4;

		return max < 0 ? 0 : max;
	}

	private boolean canScroll(int delta)
	{
		if (offset + delta < 0)
		{
			return false;
		}

		return offset + delta <= getMaxOffset();
	}

	private boolean isHoveringOverValidSlot()
	{
		return grid.isConnected() && isHoveringOverSlot() && hoveringSlotId < getItems().size();
	}

	private boolean isHoveringOverSlot()
	{
		return hoveringSlotId >= 0;
	}

	public boolean isHoveringOverClear(int mouseX, int mouseY)
	{
		return inBounds(81, 105, 7, 7, mouseX, mouseY);
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		if (grid.getType() == EnumGridType.CRAFTING)
		{
			bindTexture("gui/crafting_grid.png");
		}
		else
		{
			bindTexture("gui/grid.png");
		}

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		searchField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		RenderHelper.enableGUIStandardItemLighting();

		drawString(7, 7, t("gui.storagecraft:grid"));

		if (grid.getType() == EnumGridType.CRAFTING)
		{
			drawString(7, 94, t("container.crafting"));
		}

		drawString(7, grid.getType() == EnumGridType.CRAFTING ? 163 : 96, t("container.inventory"));

		int x = 8;
		int y = 20;

		List<StorageItem> items = getItems();

		hoveringSlotId = -1;

		int slot = offset * 9;

		for (int i = 0; i < 9 * 4; ++i)
		{
			if (slot < items.size())
			{
				drawItem(x, y, items.get(slot).toItemStack(), true);
			}

			if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected())
			{
				hoveringSlotId = slot;

				if (slot < items.size())
				{
					// We need to use the ID, because if we filter, the client-side index will change
					// while the serverside's index will still be the same.
					hoveringId = items.get(slot).getId();
				}

				int color = grid.isConnected() ? -2130706433 : 0xFF5B5B5B;

				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				zLevel = 190;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(x, y, x + 16, y + 16, color, color);
				zLevel = 0;
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}

			slot++;

			x += 18;

			if ((i + 1) % 9 == 0)
			{
				x = 8;
				y += 18;
			}
		}

		if (isHoveringOverValidSlot())
		{
			drawTooltip(mouseX, mouseY, getItems().get(hoveringSlotId).toItemStack());
		}

		if (grid.getType() == EnumGridType.CRAFTING && isHoveringOverClear(mouseX, mouseY))
		{
			drawTooltip(mouseX, mouseY, t("misc.storagecraft:clear"));
		}

		RenderHelper.disableStandardItemLighting();
	}

	public List<StorageItem> getItems()
	{
		List<StorageItem> items = new ArrayList<StorageItem>();

		if (!grid.isConnected())
		{
			return items;
		}

		items.addAll(grid.getController().getItems());

		if (!searchField.getText().trim().isEmpty())
		{
			Iterator<StorageItem> t = items.iterator();

			while (t.hasNext())
			{
				StorageItem item = t.next();

				if (!item.toItemStack().getDisplayName().toLowerCase().contains(searchField.getText().toLowerCase()))
				{
					t.remove();
				}
			}
		}

		switch (SORTING_TYPE)
		{
			case SORTING_TYPE_COUNT:
				items.sort(new Comparator<StorageItem>()
				{
					@Override
					public int compare(StorageItem o1, StorageItem o2)
					{
						switch (SORTING_DIRECTION)
						{
							case SORTING_DIRECTION_ASCENDING:
								return Integer.valueOf(o2.getQuantity()).compareTo(o1.getQuantity());
							case SORTING_DIRECTION_DESCENDING:
								return Integer.valueOf(o1.getQuantity()).compareTo(o2.getQuantity());
							default:
								return 0;
						}
					}
				});

				break;
			case SORTING_TYPE_NAME:
				items.sort(new Comparator<StorageItem>()
				{
					@Override
					public int compare(StorageItem o1, StorageItem o2)
					{
						switch (SORTING_DIRECTION)
						{
							case SORTING_DIRECTION_ASCENDING:
								return o2.toItemStack().getDisplayName().compareTo(o1.toItemStack().getDisplayName());
							case SORTING_DIRECTION_DESCENDING:
								return o1.toItemStack().getDisplayName().compareTo(o2.toItemStack().getDisplayName());
							default:
								return 0;
						}
					}
				});

				break;
		}

		return items;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, clickedButton);

		boolean clickedClear = grid.getType() == EnumGridType.CRAFTING && clickedButton == 0 && isHoveringOverClear(mouseX - guiLeft, mouseY - guiTop);

		if (grid.isConnected())
		{
			TileController controller = grid.getController();

			if (isHoveringOverSlot() && container.getPlayer().inventory.getItemStack() != null)
			{
				StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), -1, clickedButton == 1));
			}
			else if (isHoveringOverValidSlot() && container.getPlayer().inventory.getItemStack() == null)
			{
				StorageCraft.NETWORK.sendToServer(new MessageStoragePull(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), hoveringId, clickedButton == 1, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)));
			}
			else if (clickedClear)
			{
				StorageCraft.NETWORK.sendToServer(new MessageGridCraftingClear(grid));
			}
			else
			{
				for (Slot slot : container.getPlayerInventorySlots())
				{
					if (inBounds(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX - guiLeft, mouseY - guiTop))
					{
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
						{
							StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), slot.slotNumber, clickedButton == 1));
						}
					}
				}
			}
		}

		if (clickedClear)
		{
			mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
		}
	}

	@Override
	protected void keyTyped(char character, int keyCode) throws IOException
	{
		if (!checkHotbarKeys(keyCode) && searchField.textboxKeyTyped(character, keyCode))
		{
		}
		else
		{
			super.keyTyped(character, keyCode);
		}
	}
}
