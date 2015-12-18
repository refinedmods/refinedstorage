package storagecraft.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import storagecraft.container.ContainerStorageProxy;
import storagecraft.tile.TileStorageProxy;

public class GuiStorageProxy extends GuiMachine {
	public static final ResourceLocation STORAGE_PROXY_RESOURCE = new ResourceLocation("storagecraft:textures/gui/storageProxy.png");

	public GuiStorageProxy(ContainerStorageProxy container, TileStorageProxy storageProxy) {
		super(container, storageProxy);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(STORAGE_PROXY_RESOURCE);

		drawTexturedModalRect((this.width - xSize) / 2, (this.height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:storageProxy"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 39, 4210752);
	}
}
