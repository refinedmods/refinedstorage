package storagecraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
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

		drawTexturedModalRect((this.width - xSize) / 2, (this.height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:drive"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 96, 4210752);
	}
}
