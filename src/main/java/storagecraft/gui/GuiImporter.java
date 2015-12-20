package storagecraft.gui;

import storagecraft.container.ContainerImporter;
import storagecraft.gui.sidebutton.SideButtonCompare;
import storagecraft.gui.sidebutton.SideButtonImporterMode;
import storagecraft.gui.sidebutton.SideButtonRedstoneMode;
import storagecraft.tile.TileImporter;
import storagecraft.util.InventoryUtils;

public class GuiImporter extends GuiBase {
	private TileImporter importer;

	public GuiImporter(ContainerImporter container, TileImporter importer) {
		super(container, 176, 137);

		this.importer = importer;
	}

	@Override
	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(importer));

		addSideButton(new SideButtonCompare(importer, "NBT", InventoryUtils.COMPARE_NBT));
		addSideButton(new SideButtonCompare(importer, "Damage", InventoryUtils.COMPARE_DAMAGE));

		addSideButton(new SideButtonImporterMode(importer));
	}

	@Override
	public void update(int x, int y) {
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/importer.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.storagecraft:importer"));
		drawString(7, 43, t("container.inventory"));
	}
}
