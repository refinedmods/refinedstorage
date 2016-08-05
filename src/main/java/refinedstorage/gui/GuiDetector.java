package refinedstorage.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiTextField;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerDetector;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonDetectorMode;
import refinedstorage.tile.TileDestructor;
import refinedstorage.tile.TileDetector;
import refinedstorage.tile.data.TileDataManager;

import java.io.IOException;

public class GuiDetector extends GuiBase {
    private GuiTextField amountField;

    public GuiDetector(ContainerDetector container) {
        super(container, 176, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonCompare(TileDestructor.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileDestructor.COMPARE, CompareUtils.COMPARE_NBT));

        addSideButton(new SideButtonDetectorMode());

        amountField = new GuiTextField(0, fontRendererObj, x + 62 + 1, y + 23 + 1, 25, fontRendererObj.FONT_HEIGHT);
        // @TODO: Change when the packet is received instead
        amountField.setText(String.valueOf(TileDetector.AMOUNT.getValue()));
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
                TileDataManager.setParameter(TileDetector.AMOUNT, result);
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }
}
