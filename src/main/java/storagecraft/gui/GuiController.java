package storagecraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import storagecraft.container.ContainerController;
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
		mc.getTextureManager().bindTexture(CONTROLLER_RESOURCE);

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int bx = 17;
		int by = 25;
		int bw = 16;
		int bh = 58;

		int nbh = (int) ((float) controller.getEnergyStored(null) / (float) controller.getMaxEnergyStored(null) * (float) bh);

		drawTexturedModalRect(x + bx, y + by + bh - nbh, 178, 0, bw, nbh);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:controller"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 96, 4210752);

		fontRendererObj.drawString(String.format(StatCollector.translateToLocal("misc.storagecraft:energyStored"), controller.getEnergyStored(null), controller.getMaxEnergyStored(null)), 45, 24, 4210752);
		fontRendererObj.drawString(String.format(StatCollector.translateToLocal("misc.storagecraft:energyUsage"), controller.getEnergyUsage()), 45, 44, 4210752);
	}
}
