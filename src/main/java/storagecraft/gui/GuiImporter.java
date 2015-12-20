package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerImporter;
import storagecraft.network.MessageImporterUpdate;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiMachine {
	public static final ResourceLocation IMPORTER_RESOURCE = new ResourceLocation("storagecraft:textures/gui/importer.png");

	private TileImporter importer;

	private GuiButton compareNBT;
	private GuiButton compareDamage;
	private GuiButton mode;

	public GuiImporter(ContainerImporter container, TileImporter importer) {
		super(container, importer);

		this.xSize = 176;
		this.ySize = 201;

		this.importer = importer;
	}

	@Override
	public void initGui() {
		super.initGui();

		int x = (this.width - xSize) / 2;
		int y = (this.height - ySize) / 2;

		buttonList.add(compareNBT = new GuiButton(1, x + 7, y + 41, 100, 20, ""));
		buttonList.add(compareDamage = new GuiButton(2, x + 7, y + 63, 120, 20, ""));
		buttonList.add(mode = new GuiButton(3, x + 7, y + 85, 80, 20, ""));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		compareNBT.displayString = getTextForCompareToggle("NBT", InventoryUtils.COMPARE_NBT);
		compareDamage.displayString = getTextForCompareToggle("Damage", InventoryUtils.COMPARE_DAMAGE);
		mode.displayString = StatCollector.translateToLocal("misc.storagecraft:importer.mode." + importer.getMode().id);
	}

	private String getTextForCompareToggle(String which, int flag) {
		StringBuilder builder = new StringBuilder();

		builder.append(StatCollector.translateToLocal("misc.storagecraft:compare" + which));
		builder.append(": ");

		if ((importer.getCompareFlags() & flag) == flag) {
			builder.append(StatCollector.translateToLocal("misc.storagecraft:on"));
		} else {
			builder.append(StatCollector.translateToLocal("misc.storagecraft:off"));
		}

		return builder.toString();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(IMPORTER_RESOURCE);

		drawTexturedModalRect((this.width - xSize) / 2, (this.height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(StatCollector.translateToLocal("gui.storagecraft:importer"), 7, 7, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 7, 108, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		int flags = importer.getCompareFlags();

		if (button.id == compareNBT.id) {
			flags ^= InventoryUtils.COMPARE_NBT;
		} else if (button.id == compareDamage.id) {
			flags ^= InventoryUtils.COMPARE_DAMAGE;
		}

		StorageCraft.NETWORK.sendToServer(new MessageImporterUpdate(importer.xCoord, importer.yCoord, importer.zCoord, flags, button.id == mode.id));
	}
}
