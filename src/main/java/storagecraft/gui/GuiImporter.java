package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import storagecraft.SC;
import storagecraft.container.ContainerImporter;
import storagecraft.network.MessageImporterUpdate;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiContainer {
	public static final ResourceLocation IMPORTER_RESOURCE = new ResourceLocation("storagecraft:textures/gui/importer.png");

	private TileImporter importer;

	private int compareFlags;

	private GuiButton compareNBT;
	private GuiButton compareDamage;

	public GuiImporter(ContainerImporter container, TileImporter importer) {
		super(container);

		this.xSize = 176;
		this.ySize = 182;

		this.importer = importer;
	}

	@Override
	public void initGui() {
		super.initGui();

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		buttonList.add(compareNBT = new GuiButton(0, x + 7, y + 41, 100, 20, "..."));
		buttonList.add(compareDamage = new GuiButton(1, x + 7, y + 63, 120, 20, "..."));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		compareFlags = importer.getCompareFlags();

		compareNBT.displayString = StatCollector.translateToLocal("misc.storagecraft:compareNBT") + ": ";
		compareNBT.displayString += ((compareFlags & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT) ? StatCollector.translateToLocal("misc.storagecraft:on") : StatCollector.translateToLocal("misc.storagecraft:off");

		compareDamage.displayString = StatCollector.translateToLocal("misc.storagecraft:compareDamage") + ": ";
		compareDamage.displayString += ((compareFlags & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE) ? StatCollector.translateToLocal("misc.storagecraft:on") : StatCollector.translateToLocal("misc.storagecraft:off");
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(IMPORTER_RESOURCE);

		drawTexturedModalRect((this.width - xSize) / 2, (this.height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:importer"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 87, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int flags = compareFlags;

		switch (button.id) {
			case 0:
				flags ^= InventoryUtils.COMPARE_NBT;
				break;
			case 1:
				flags ^= InventoryUtils.COMPARE_DAMAGE;
				break;
		}

		SC.NETWORK.sendToServer(new MessageImporterUpdate(importer.xCoord, importer.yCoord, importer.zCoord, flags));
	}
}
