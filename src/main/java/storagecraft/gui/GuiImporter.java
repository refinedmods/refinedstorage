package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerImporter;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.network.MessageImporterUpdate;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiBase {
	private TileImporter importer;

	private GuiButton compareNBTButton;
	private GuiButton compareDamageButton;
	private GuiButton modeButton;

	public GuiImporter(ContainerImporter container, TileImporter importer) {
		super(container, 176, 201);

		this.importer = importer;
	}

	@Override
	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(importer));

		compareNBTButton = addButton(x + 7, y + 41, 100, 20);
		compareDamageButton = addButton(x + 7, y + 63, 120, 20);
		modeButton = addButton(x + 7, y + 85, 80, 20);
	}

	@Override
	public void update(int x, int y) {
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
		drawString(7, 7, t("gui.storagecraft:importer"));
		drawString(7, 108, t("container.inventory"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		int flags = importer.getCompareFlags();

		if (button == compareNBTButton) {
			flags ^= InventoryUtils.COMPARE_NBT;
		} else if (button == compareDamageButton) {
			flags ^= InventoryUtils.COMPARE_DAMAGE;
		}

		StorageCraft.NETWORK.sendToServer(new MessageImporterUpdate(importer.xCoord, importer.yCoord, importer.zCoord, flags, button.id == modeButton.id));
	}
}
