package storagecraft.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import storagecraft.inventory.ContainerController;
import storagecraft.tile.TileController;

public class GuiController extends GuiContainer {
	public static final ResourceLocation CONTROLLER_RESOURCE = new ResourceLocation("storagecraft:textures/gui/controller.png");

	private TileController controller;

	public GuiController(ContainerController container, TileController controller) {
		super(container);

		this.controller = controller;

		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(CONTROLLER_RESOURCE);

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int barWidth = 16;
		int barHeight = 58;
		int barX = x + 17;
		int barY = y + 25;

		int energy = controller.getEnergyStored(null);
		int maxEnergy = controller.getMaxEnergyStored(null);

		int newBarHeight = (int) ((float) energy / (float) maxEnergy * (float) barHeight);

		drawTexturedModalRect(barX, barY + barHeight - newBarHeight, 178, 0, barWidth, newBarHeight);

		fontRendererObj.drawString("Controller", x + 7, y + 7, 4210752);
		fontRendererObj.drawString("Inventory", x + 7, y + 96, 4210752);
		fontRendererObj.drawString("Energy usage: " + controller.getEnergyUsage() + " RF/t", x + 45, y + 24, 4210752);

		if (mouseX >= barX && mouseX <= barX + barWidth && mouseY >= barY && mouseY <= barY + barHeight) {
			List<String> lines = new ArrayList<String>();

			lines.add(energy + " / " + maxEnergy + " RF");

			drawHoveringText(lines, mouseX, mouseY, fontRendererObj);
		}
	}
}
