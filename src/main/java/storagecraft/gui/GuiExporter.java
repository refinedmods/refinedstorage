package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerExporter;
import storagecraft.network.MessageExporterUpdate;
import storagecraft.tile.TileExporter;
import storagecraft.util.InventoryUtils;

public class GuiExporter extends GuiMachine {
	private TileExporter exporter;

	private GuiButton compareNBT;
	private GuiButton compareDamage;

	public GuiExporter(ContainerExporter container, TileExporter exporter) {
		super(container, 176, 186, exporter);

		this.exporter = exporter;
	}

	@Override
	public void init(int x, int y) {
		super.init(x, y);

		buttonList.add(compareNBT = new GuiButton(1, x + 7, y + 41, 100, 20, ""));
		buttonList.add(compareDamage = new GuiButton(2, x + 7, y + 63, 120, 20, ""));
	}

	@Override
	public void update(int x, int y) {
		super.update(x, y);

		compareNBT.displayString = t("misc.storagecraft:compareNBT") + ": ";
		compareNBT.displayString += t("misc.storagecraft:" + ((exporter.getCompareFlags() & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT ? "on" : "off"));

		compareDamage.displayString = t("misc.storagecraft:compareDamage") + ": ";
		compareDamage.displayString += t("misc.storagecraft:" + ((exporter.getCompareFlags() & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE ? "on" : "off"));
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/exporter.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:exporter"));
		drawString(7, 93, t("container.inventory"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		int flags = exporter.getCompareFlags();

		if (button.id == compareNBT.id) {
			flags ^= InventoryUtils.COMPARE_NBT;
		} else if (button.id == compareDamage.id) {
			flags ^= InventoryUtils.COMPARE_DAMAGE;
		}

		StorageCraft.NETWORK.sendToServer(new MessageExporterUpdate(exporter.xCoord, exporter.yCoord, exporter.zCoord, flags));
	}
}
