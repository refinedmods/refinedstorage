package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageDetectorModeUpdate;
import storagecraft.tile.TileDetector;

public class SideButtonDetectorMode extends SideButton {
	private TileDetector detector;

	public SideButtonDetectorMode(TileDetector detector) {
		this.detector = detector;
	}

	@Override
	public String getTooltip(GuiBase gui) {
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.GREEN).append(gui.t("sidebutton.storagecraft:detector.mode")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:detector.mode." + detector.getMode()));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y) {
	}

	@Override
	public void actionPerformed() {
		StorageCraft.NETWORK.sendToServer(new MessageDetectorModeUpdate(detector));
	}
}
