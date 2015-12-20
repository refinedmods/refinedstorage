package storagecraft.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerGrid;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiMachine {
	private ContainerGrid container;
	private TileGrid grid;

	private GuiTextField searchField;

	private int hoveringSlotId;
	private int hoveringId;

	private int offset;

	public GuiGrid(ContainerGrid container, TileGrid grid) {
		super(container, 176, 190, grid);

		this.container = container;
		this.grid = grid;
	}

	@Override
	public void init(int x, int y) {
		super.init(x, y);

		searchField = new GuiTextField(fontRendererObj, x + 80 + 2, y + 6 + 1, 88 - 6, fontRendererObj.FONT_HEIGHT);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setVisible(true);
		searchField.setTextColor(16777215);
		searchField.setCanLoseFocus(false);
		searchField.setFocused(true);
	}

	@Override
	public void update(int x, int y) {
		super.update(x, y);

		int wheel = Mouse.getDWheel();

		wheel = Math.max(Math.min(-wheel, 1), -1);

		if (canScroll(wheel)) {
			offset += wheel;
		}

		if (offset > getMaxOffset()) {
			offset = getMaxOffset();
		}
	}

	private int getMaxOffset() {
		if (!grid.isConnected()) {
			return 0;
		}

		int max = ((int) Math.ceil((float) getItems().size() / (float) 9)) - 4;

		return max < 0 ? 0 : max;
	}

	private boolean canScroll(int delta) {
		if (offset + delta < 0) {
			return false;
		}

		return offset + delta <= getMaxOffset();
	}

	private boolean isHoveringOverValidSlot() {
		return grid.isConnected() && isHoveringOverSlot() && hoveringSlotId < getItems().size();
	}

	private boolean isHoveringOverSlot() {
		return hoveringSlotId >= 0;
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/grid.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		searchField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:grid"));
		drawString(7, 96, t("container.inventory"));

		int x = 8;
		int y = 20;

		List<StorageItem> items = getItems();

		hoveringSlotId = -1;

		int slot = offset * 9;

		for (int i = 0; i < 9 * 4; ++i) {
			if (slot < items.size()) {
				drawItem(x, y, items.get(slot).toItemStack(), true);
			}

			if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
				hoveringSlotId = slot;

				if (slot < items.size()) {
					// We need to use the ID, because if we filter, the client-side index will change
					// while the serverside's index will still be the same.
					hoveringId = items.get(slot).getId();
				}

				int color = grid.isConnected() ? -2130706433 : 0xFF5B5B5B;

				zLevel = 190;
				drawGradientRect(x, y, x + 16, y + 16, color, color);
				zLevel = 0;
			}

			slot++;

			x += 18;

			if ((i + 1) % 9 == 0) {
				x = 8;
				y += 18;
			}
		}

		if (isHoveringOverValidSlot()) {
			drawTooltip(mouseX, mouseY, items.get(hoveringSlotId).toItemStack());
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
		super.mouseClicked(mouseX, mouseY, clickedButton);

		if (grid.isConnected()) {
			TileController controller = grid.getController();

			if (isHoveringOverSlot() && container.getPlayer().inventory.getItemStack() != null) {
				StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.xCoord, controller.yCoord, controller.zCoord, -1, clickedButton == 1));
			} else if (isHoveringOverValidSlot() && container.getPlayer().inventory.getItemStack() == null) {
				StorageCraft.NETWORK.sendToServer(new MessageStoragePull(controller.xCoord, controller.yCoord, controller.zCoord, hoveringId, clickedButton == 1, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)));
			} else {
				for (int i = 0; i < container.inventorySlots.size(); ++i) {
					Slot slot = (Slot) container.inventorySlots.get(i);

					if (inBounds(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX - guiLeft, mouseY - guiTop)) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
							StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.xCoord, controller.yCoord, controller.zCoord, slot.slotNumber, clickedButton == 1));
						}
					}
				}
			}
		}
	}

	@Override
	protected void keyTyped(char character, int keyCode) {
		if (!checkHotbarKeys(keyCode) && searchField.textboxKeyTyped(character, keyCode)) {
		} else {
			super.keyTyped(character, keyCode);
		}
	}

	public List<StorageItem> getItems() {
		List<StorageItem> items = new ArrayList<StorageItem>();

		if (!grid.isConnected()) {
			return items;
		}

		items.addAll(grid.getController().getItems());

		if (!searchField.getText().trim().isEmpty()) {
			Iterator<StorageItem> t = items.iterator();

			while (t.hasNext()) {
				StorageItem item = t.next();

				if (!item.toItemStack().getDisplayName().toLowerCase().contains(searchField.getText().toLowerCase())) {
					t.remove();
				}
			}
		}

		return items;
	}
}
