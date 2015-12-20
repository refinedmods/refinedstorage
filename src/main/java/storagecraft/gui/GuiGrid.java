package storagecraft.gui;

import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerGrid;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiMachine {
	private ContainerGrid container;
	private TileGrid grid;

	private int hoveringSlot;
	private int offset;

	public GuiGrid(ContainerGrid container, TileGrid grid) {
		super(container, 176, 190, grid);

		this.container = container;
		this.grid = grid;
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

		int max = ((int) Math.ceil((float) grid.getController().getItems().size() / (float) 9)) - 4;

		return max < 0 ? 0 : max;
	}

	private boolean canScroll(int delta) {
		if (offset + delta < 0) {
			return false;
		}

		return offset + delta <= getMaxOffset();
	}

	private boolean isHoveringOverValidSlot() {
		return grid.isConnected() && isHoveringOverSlot() && hoveringSlot < grid.getController().getItems().size();
	}

	private boolean isHoveringOverSlot() {
		return hoveringSlot >= 0;
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/grid.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:grid"));
		drawString(7, 96, t("container.inventory"));

		int x = 8;
		int y = 20;

		hoveringSlot = -1;

		int slot = offset * 9;

		for (int i = 0; i < 9 * 4; ++i) {
			if (grid.isConnected() && slot < grid.getController().getItems().size()) {
				drawItem(x, y, grid.getController().getItems().get(slot).toItemStack(), true);
			}

			if ((mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) || !grid.isConnected()) {
				hoveringSlot = slot;

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
			drawTooltip(mouseX, mouseY, grid.getController().getItems().get(hoveringSlot).toItemStack());
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
				StorageCraft.NETWORK.sendToServer(new MessageStoragePull(controller.xCoord, controller.yCoord, controller.zCoord, hoveringSlot, clickedButton == 1, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)));
			} else {
				for (int i = 0; i < container.inventorySlots.size(); ++i) {
					Slot slot = (Slot) container.inventorySlots.get(i);

					if (func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
							StorageCraft.NETWORK.sendToServer(new MessageStoragePush(controller.xCoord, controller.yCoord, controller.zCoord, slot.slotNumber, clickedButton == 1));
						}
					}
				}
			}
		}
	}
}
