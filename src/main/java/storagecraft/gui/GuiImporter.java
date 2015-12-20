package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerImporter;
import storagecraft.network.MessageImporterUpdate;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiMachine {
	private TileImporter importer;

	private GuiButton compareNBTButton;
	private GuiButton compareDamageButton;
	private GuiButton modeButton;

	public GuiImporter(ContainerImporter container, TileImporter importer) {
		super(container, 176, 201, importer);

		this.importer = importer;
	}

	@Override
	public void init(int x, int y) {
		super.init(x, y);

		buttonList.add(compareNBTButton = new GuiButton(1, x + 7, y + 41, 100, 20, ""));
		buttonList.add(compareDamageButton = new GuiButton(2, x + 7, y + 63, 120, 20, ""));
		buttonList.add(modeButton = new GuiButton(3, x + 7, y + 85, 80, 20, ""));
	}

	@Override
	public void update(int x, int y) {
		super.update(x, y);

		compareNBTButton.displayString = t("misc.storagecraft:compareNBT") + ": ";
		compareNBTButton.displayString += t("misc.storagecraft:" + ((importer.getCompareFlags() & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT ? "on" : "off"));

		compareDamageButton.displayString = t("misc.storagecraft:compareDamage") + ": ";
		compareDamageButton.displayString += t("misc.storagecraft:" + ((importer.getCompareFlags() & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE ? "on" : "off"));

		modeButton.displayString = t("misc.storagecraft:importer.mode." + importer.getMode());
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/importer.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		drawString(7, 7, t("gui.storagecraft:importer"));
		drawString(7, 108, t("container.inventory"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		int flags = importer.getCompareFlags();

		if (button.id == compareNBTButton.id) {
			flags ^= InventoryUtils.COMPARE_NBT;
		} else if (button.id == compareDamageButton.id) {
			flags ^= InventoryUtils.COMPARE_DAMAGE;
		}

		StorageCraft.NETWORK.sendToServer(new MessageImporterUpdate(importer.xCoord, importer.yCoord, importer.zCoord, flags, button.id == modeButton.id));
	}
}
