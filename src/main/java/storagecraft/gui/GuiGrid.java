package storagecraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import storagecraft.SC;
import storagecraft.inventory.ContainerGrid;
import storagecraft.network.MessagePullFromStorage;
import storagecraft.network.MessagePushToStorage;
import storagecraft.tile.TileController;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiContainer {
	public static final ResourceLocation GRID_RESOURCE = new ResourceLocation("storagecraft:textures/gui/grid.png");

	private ContainerGrid container;
	private TileGrid grid;

	public GuiGrid(ContainerGrid container, TileGrid grid) {
		super(container);

		this.container = container;
		this.grid = grid;

		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(GRID_RESOURCE);

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:grid"), x + 7, y + 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), x + 7, y + 96, 4210752);

		int xx = getGridXStart();
		int yy = getGridYStart();

		ItemStack toolTip = null;

		for (int i = 0; i < 9 * 4; ++i) {
			ItemStack stack = null;

			if (grid.isConnected() && i < grid.getController().getItems().size()) {
				stack = grid.getController().getItems().get(i).toItemStack();

				itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, xx, yy);
				itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.getTextureManager(), stack, xx, yy);
			}

			if ((mouseX >= xx && mouseX <= xx + 16 && mouseY >= yy && mouseY <= yy + 16) || !grid.isConnected()) {
				int color = grid.isConnected() ? -2130706433 : 0xFF5B5B5B;

				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColorMask(true, true, true, false);
				drawGradientRect(xx, yy, xx + 16, yy + 16, color, color);
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);

				if (stack != null) {
					toolTip = stack;
				}
			}

			xx += 18;

			if ((i + 1) % 9 == 0) {
				xx = getGridXStart();
				yy += 18;
			}
		}

		if (toolTip != null) {
			renderToolTip(toolTip, mouseX, mouseY);
		}
	}

	private int getGridXStart() {
		return ((this.width - xSize) / 2) + 8;
	}

	private int getGridXEnd() {
		return getGridXStart() + (18 * 9);
	}

	private int getGridYStart() {
		return ((this.height - ySize) / 2) + 20;
	}

	private int getGridYEnd() {
		return getGridYStart() + (18 * 4);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
		super.mouseClicked(mouseX, mouseY, clickedButton);

		if (grid.isConnected()) {
			TileController controller = grid.getController();

			if (mouseX >= getGridXStart() && mouseX <= getGridXEnd() && mouseY >= getGridYStart() && mouseY <= getGridYEnd()) {
				if (container.getPlayer().inventory.getItemStack() != null) {
					SC.NETWORK.sendToServer(new MessagePushToStorage(controller.xCoord, controller.yCoord, controller.zCoord, -1, clickedButton == 1));
				} else {
					int slotX = ((mouseX - getGridXStart()) / 18) + 1;
					int slotY = ((mouseY - getGridYStart()) / 18) + 1;
					int slotId = (slotX * slotY) - 1;

					SC.NETWORK.sendToServer(new MessagePullFromStorage(controller.xCoord, controller.yCoord, controller.zCoord, slotId, clickedButton == 1, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)));
				}
			} else {
				for (int i = 0; i < container.inventorySlots.size(); ++i) {
					Slot slot = (Slot) container.inventorySlots.get(i);

					if (func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
							SC.NETWORK.sendToServer(new MessagePushToStorage(controller.xCoord, controller.yCoord, controller.zCoord, slot.slotNumber, clickedButton == 1));
						}
					}
				}
			}
		}
	}
}
