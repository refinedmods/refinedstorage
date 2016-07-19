package refinedstorage.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiTextField;
import refinedstorage.RefinedStorage;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerDetector;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonDetectorMode;
import refinedstorage.network.MessageDetectorAmountUpdate;
import refinedstorage.tile.TileDetector;

import java.io.IOException;

public class GuiDetector extends GuiBase {
    private TileDetector detector;

    private GuiTextField amountField;

    public GuiDetector(ContainerDetector container, TileDetector detector) {
        super(container, 176, 137);

        this.detector = detector;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonCompare(detector, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(detector, CompareUtils.COMPARE_NBT));

        addSideButton(new SideButtonDetectorMode(detector));

        amountField = new GuiTextField(0, fontRendererObj, x + 62 + 1, y + 23 + 1, 25, fontRendererObj.FONT_HEIGHT);
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

        drawTexture(x, y, 0, 0, width, height);

        amountField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:detector"));
        drawString(7, 43, t("container.inventory"));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
            Integer result = Ints.tryParse(amountField.getText());

            if (result != null) {
                RefinedStorage.INSTANCE.network.sendToServer(new MessageDetectorAmountUpdate(detector, result));
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }
}
