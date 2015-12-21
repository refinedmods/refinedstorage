package storagecraft.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiTextField;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerDetector;
import storagecraft.gui.sidebutton.SideButtonCompare;
import storagecraft.gui.sidebutton.SideButtonDetectorMode;
import storagecraft.network.MessageDetectorAmountUpdate;
import storagecraft.tile.TileDetector;
import storagecraft.util.InventoryUtils;

public class GuiDetector extends GuiBase {
	private TileDetector detector;

	private GuiTextField amountField;

	public GuiDetector(ContainerDetector container, TileDetector detector) {
		super(container, 176, 137);

		this.detector = detector;
	}

	@Override
	public void init(int x, int y) {
		addSideButton(new SideButtonCompare(detector, InventoryUtils.COMPARE_DAMAGE));
		addSideButton(new SideButtonCompare(detector, InventoryUtils.COMPARE_NBT));

		addSideButton(new SideButtonDetectorMode(detector));

		amountField = new GuiTextField(fontRendererObj, x + 62 + 1, y + 23 + 1, 25, fontRendererObj.FONT_HEIGHT);
		amountField.setText(String.valueOf(detector.getAmount()));
		amountField.setEnableBackgroundDrawing(false);
		amountField.setVisible(true);
		amountField.setTextColor(16777215);
		amountField.setCanLoseFocus(false);
		amountField.setFocused(true);
	}

	@Override
	public void update(int x, int y) {
	}

	@Override
	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture("gui/detector.png");

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		amountField.drawTextBox();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.storagecraft:detector"));
		drawString(7, 43, t("container.inventory"));
	}

	@Override
	protected void keyTyped(char character, int keyCode) {
		if (!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
			Integer result = Ints.tryParse(amountField.getText());

			if (result != null) {
				StorageCraft.NETWORK.sendToServer(new MessageDetectorAmountUpdate(detector, result));
			}
		} else {
			super.keyTyped(character, keyCode);
		}
	}
}
