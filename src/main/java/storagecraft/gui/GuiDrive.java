package storagecraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import storagecraft.inventory.ContainerDrive;

public class GuiDrive extends GuiContainer {
	public static final ResourceLocation DRIVE_RESOURCE = new ResourceLocation("storagecraft:textures/gui/drive.png");

	public GuiDrive(ContainerDrive container) {
		super(container);

		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(DRIVE_RESOURCE);

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		fontRendererObj.drawString("Drive", x + 7, y + 7, 4210752);
		fontRendererObj.drawString("Inventory", x + 7, y + 96, 4210752);
	}
}
