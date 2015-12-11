package storagecraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import storagecraft.SC;
import storagecraft.network.MessagePushToStorage;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileGrid;

public class GuiGrid extends GuiContainer {
	public static final ResourceLocation GRID_RESOURCE = new ResourceLocation("storagecraft:textures/gui/grid.png");

	private TileGrid grid;

	public GuiGrid(Container container, TileGrid grid) {
		super(container);

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

		fontRendererObj.drawString("Grid", x + 7, y + 7, 4210752);
		fontRendererObj.drawString("Inventory", x + 7, y + 96, 4210752);

		if (grid.isConnected()) {
			int xx = getGridXStart();
			int yy = getGridYStart();

			for (int i = 0; i < grid.getController().getStorage().all().size(); ++i) {
				StorageItem item = grid.getController().getStorage().all().get(i);

				ItemStack stack = new ItemStack(item.getType(), item.getQuantity(), item.getMeta());

				itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), stack, xx, yy);
				itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.getTextureManager(), stack, xx, yy);

				xx += 18;
			}
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

		if (clickedButton == 0) {
			if (mouseX > getGridXStart() && mouseX < getGridXEnd() && mouseY > getGridYStart() && mouseY < getGridYEnd()) {
				SC.NETWORK.sendToServer(new MessagePushToStorage(grid.getController()));
			}
		}
	}
}
