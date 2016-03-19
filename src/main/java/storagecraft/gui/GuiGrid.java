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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import storagecraft.StorageCraft;
import storagecraft.block.EnumGridType;
import storagecraft.container.ContainerGrid;
import storagecraft.gui.sidebutton.SideButtonGridSortingDirection;
import storagecraft.gui.sidebutton.SideButtonGridSortingType;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.item.ItemPattern;
import storagecraft.network.MessageGridCraftingClear;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiBase
{
	private ContainerGrid container;
	private TileGrid grid;

	private GuiTextField searchField;

	private int hoveringSlotId;
	private int hoveringId;

	private int offset;

	public GuiGrid(ContainerGrid container, TileGrid grid)
	{
		super(container, 176, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 256 : 190);

		this.container = container;
		this.grid = grid;
	}

	@Override
	public void init(int x, int y)
	{
		addSideButton(new SideButtonRedstoneMode(grid));

		addSideButton(new SideButtonGridSortingDirection(grid));
		addSideButton(new SideButtonGridSortingType(grid));

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

	private boolean isHoveringOverValidSlot(List<StorageItem> items)
	{
		return grid.isConnected() && isHoveringOverSlot() && hoveringSlotId < items.size();
	}

	private boolean isHoveringOverSlot()
	{
		return hoveringSlotId >= 0;
	}

	public boolean isHoveringOverClear(int mouseX, int mouseY)
	{
		if (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN)
		{
			return inBounds(81, 105, 7, 7, mouseX, mouseY);
		}

		return false;
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY)
	{
		if (grid.getType() == EnumGridType.CRAFTING)
		{
			bindTexture("gui/crafting_grid.png");
		}
		else if (grid.getType() == EnumGridType.PATTERN)
		{
			bindTexture("gui/pattern_grid.png");
		}
		else
		{
			bindTexture("gui/grid.png");
		}

		drawTexture(x, y, 0, 0, width, height);

		searchField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		drawString(7, 7, t("gui.storagecraft:grid"));

		if (grid.getType() == EnumGridType.CRAFTING)
		{
			drawString(7, 94, t("container.crafting"));
		}
		else if (grid.getType() == EnumGridType.PATTERN)
		{
			drawString(7, 94, t("gui.storagecraft:grid.pattern"));
		}

		drawString(7, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 163 : 96, t("container.inventory"));

		int x = 8;
		int y = 20;

		List<StorageItem> items = getItems();

		hoveringSlotId = -1;

		int slot = offset * 9;

		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < 9 * 4; ++i)
		{
			if (slot < items.size())
			{
				if (items.get(slot).isCraftable())
				{
					drawItem(x, y, items.get(slot).toItemStack(), false);
					drawString(x, y, "Craft", 0xFFFFFFFF);
				}
				else
				{
					drawItem(x, y, items.get(slot).toItemStack(), true);
				}
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

		if (isHoveringOverValidSlot(items))
		{
			drawTooltip(mouseX, mouseY, items.get(hoveringSlotId).toItemStack());
		}

		if (isHoveringOverClear(mouseX, mouseY))
		{
			drawTooltip(mouseX, mouseY, t("misc.storagecraft:clear"));
		}
	}

	public List<StorageItem> getItems()
	{
		List<StorageItem> items = new ArrayList<StorageItem>();

		if (!grid.isConnected())
		{
			return items;
		}

		items.addAll(grid.getController().getItems());

		for (ItemStack pattern : grid.getController().getPatterns())
		{
			items.add(new StorageItem(ItemPattern.getPatternResult(grid.getWorld(), pattern), true));
		}

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

		items.sort(new Comparator<StorageItem>()
		{
			@Override
			public int compare(StorageItem o1, StorageItem o2)
			{
				if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING)
				{
					return o2.toItemStack().getDisplayName().compareTo(o1.toItemStack().getDisplayName());
				}
				else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING)
				{
					return o1.toItemStack().getDisplayName().compareTo(o2.toItemStack().getDisplayName());
				}

				return 0;
			}
		});

		if (grid.getSortingType() == TileGrid.SORTING_TYPE_QUANTITY)
		{
			items.sort(new Comparator<StorageItem>()
			{
				@Override
				public int compare(StorageItem o1, StorageItem o2)
				{
					if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING)
					{
						return Integer.valueOf(o2.getQuantity()).compareTo(o1.getQuantity());
					}
					else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING)
					{
						return Integer.valueOf(o1.getQuantity()).compareTo(o2.getQuantity());
					}

					return 0;
				}
			});
		}

		return items;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, clickedButton);

		boolean clickedClear = clickedButton == 0 && isHoveringOverClear(mouseX - guiLeft, mouseY - guiTop);

		if (grid.isConnected())
		{
			TileController controller = grid.getController();

			if (isHoveringOverSlot() && container.getPlayer().inventory.getItemStack() != null)
			{
				StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), -1, clickedButton == 1));
			}
			else if (isHoveringOverValidSlot(getItems()) && container.getPlayer().inventory.getItemStack() == null)
			{
				boolean half = clickedButton == 1;
				boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
				boolean one = clickedButton == 2;

				StorageCraft.NETWORK.sendToServer(new MessageStoragePull(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), hoveringId, half, one, shift));
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
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation("gui.button.press")), 1.0F));
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
