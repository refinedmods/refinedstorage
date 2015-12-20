package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageImporterModeUpdate;
import storagecraft.tile.TileImporter;

public class SideButtonImporterMode extends SideButton {
	private TileImporter importer;

	public SideButtonImporterMode(TileImporter importer) {
		this.importer = importer;
	}

	@Override
	public String getTooltip(GuiBase gui) {
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.GREEN).append(gui.t("sidebutton.storagecraft:importer.mode")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:importer.mode." + importer.getMode()));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y) {
	}

	@Override
	public void actionPerformed() {
		StorageCraft.NETWORK.sendToServer(new MessageImporterModeUpdate(importer));
	}
}
