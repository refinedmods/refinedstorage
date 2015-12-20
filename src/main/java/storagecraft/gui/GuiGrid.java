package storagecraft.gui;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerGrid;
import storagecraft.network.MessageStoragePull;
import storagecraft.network.MessageStoragePush;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiMachine {
	public static final ResourceLocation GRID_RESOURCE = new ResourceLocation("storagecraft:textures/gui/grid.png");

	private ContainerGrid container;
	private TileGrid grid;

	private int offset;

	private int hoveringSlot;

	public GuiGrid(ContainerGrid container, TileGrid grid) {
		super(container, grid);

		this.container = container;
		this.grid = grid;

		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		int wheel = Mouse.getDWheel();

		wheel = Math.max(Math.min(-wheel, 1), -1);

		if (canScroll(wheel)) {
			offset += wheel;
		}

		if (offset > getMaxOffset()) {
			offset = getMaxOffset();
		}
	}

	public int getMaxOffset() {
		if (!grid.isConnected()) {
			return 0;
		}

		int max = ((int) Math.ceil((float) grid.getController().getItems().size() / (float) 9)) - 4;

		return max < 0 ? 0 : max;
	}

	public boolean canScroll(int delta) {
		if (offset + delta < 0) {
			return false;
		}

		return offset + delta <= getMaxOffset();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(GRID_RESOURCE);

		drawTexturedModalRect((this.width - xSize) / 2, (this.height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:grid"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 96, 4210752);

		int mx = mouseX - ((this.width - xSize) / 2);
		int my = mouseY - ((this.height - ySize) / 2);

		int x = 8;
		int y = 20;

		hoveringSlot = -1;

		int slot = offset * 9;

		for (int i = 0; i < 9 * 4; ++i) {
			if (grid.isConnected() && slot < grid.getController().getItems().size()) {
				ItemStack stack = grid.getController().getItems().get(slot).toItemStack();

				itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y);
				itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y);
			}

			if ((mx >= x && mx <= x + 16 && my >= y && my <= y + 16) || !grid.isConnected()) {
				hoveringSlot = slot;

				int color = grid.isConnected() ? -2130706433 : 0xFF5B5B5B;

				drawGradientRect(x, y, x + 16, y + 16, color, color);
			}

			slot++;

			x += 18;

			if ((i + 1) % 9 == 0) {
				x = 8;
				y += 18;
			}
		}

		if (isHoveringOverValidSlot()) {
			renderToolTip(grid.getController().getItems().get(hoveringSlot).toItemStack(), mx, my);
		}
	}

	private boolean isHoveringOverValidSlot() {
		return grid.isConnected() && isHoveringOverSlot() && hoveringSlot < grid.getController().getItems().size();
	}

	private boolean isHoveringOverSlot() {
		return hoveringSlot >= 0;
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
