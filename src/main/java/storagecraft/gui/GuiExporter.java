package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerExporter;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.network.MessageExporterUpdate;
import storagecraft.tile.TileExporter;
import storagecraft.util.InventoryUtils;

public class GuiExporter extends GuiBase {
	private TileExporter exporter;

	private GuiButton compareNBTButton;
	private GuiButton compareDamageButton;

	public GuiExporter(ContainerExporter container, TileExporter exporter) {
		super(container, 176, 186);

		this.exporter = exporter;
	}

	@Override
	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(exporter));

		compareNBTButton = addButton(x + 7, y + 41, 100, 20);
		compareDamageButton = addButton(x + 7, y + 63, 120, 20);
	}

	@Override
	public void update(int x, int y) {
		compareNBTButton.displayString = t("misc.storagecraft:compareNBT") + ": ";
		compareNBTButton.displayString += t("misc.storagecraft:" + ((exporter.getCompareFlags() & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT ? "on" : "off"));

		compareDamageButton.displayString = t("misc.storagecraft:compareDamage") + ": ";
		compareDamageButton.displayString += t("misc.storagecraft:" + ((exporter.getCompareFlags() & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE ? "on" : "off"));
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/exporter.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.storagecraft:exporter"));
		drawString(7, 93, t("container.inventory"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		int flags = exporter.getCompareFlags();

		if (button == compareNBTButton) {
			flags ^= InventoryUtils.COMPARE_NBT;
		} else if (button == compareDamageButton) {
			flags ^= InventoryUtils.COMPARE_DAMAGE;
		}

		StorageCraft.NETWORK.sendToServer(new MessageExporterUpdate(exporter.xCoord, exporter.yCoord, exporter.zCoord, flags));
	}
}
