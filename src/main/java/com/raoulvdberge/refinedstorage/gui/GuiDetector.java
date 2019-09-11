package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerDetector;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonDetectorMode;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;

public class GuiDetector extends GuiBase<ContainerDetector> {
    private TextFieldWidget amount;

    public GuiDetector(ContainerDetector container, PlayerInventory inventory) {
        super(container, 176, 137, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonType(this, TileDetector.TYPE));

        addSideButton(new SideButtonDetectorMode(this));

        addSideButton(new SideButtonCompare(this, TileDetector.COMPARE, IComparer.COMPARE_NBT));

        amount = new TextFieldWidget(font, x + 41 + 1, y + 23 + 1, 50, font.FONT_HEIGHT, "");
        amount.setText(String.valueOf(TileDetector.AMOUNT.getValue()));
        amount.setEnableBackgroundDrawing(false);
        amount.setVisible(true);
        amount.setTextColor(16777215);
        amount.setCanLoseFocus(true);
        amount.setFocused2(false);
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/detector.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        amount.renderButton(0, 0, 0);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:detector"));
        drawString(7, 43, t("container.inventory"));
    }

    /* TODO
    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && amount.textboxKeyTyped(character, keyCode)) {
            Integer result = Ints.tryParse(amount.getText());

            if (result != null) {
                TileDataManager.setParameter(TileDetector.AMOUNT, result);
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        amount.mouseClicked(mouseX, mouseY, mouseButton);
    }*/

    public TextFieldWidget getAmount() {
        return amount;
    }
}
