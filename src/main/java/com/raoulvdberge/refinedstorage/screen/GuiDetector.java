package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.DetectorContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonDetectorMode;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiDetector extends BaseScreen<DetectorContainer> {
    private TextFieldWidget amount;

    public GuiDetector(DetectorContainer container, PlayerInventory inventory) {
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
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/detector.png");

        blit(x, y, 0, 0, xSize, ySize);

        amount.renderButton(0, 0, 0);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:detector"));
        renderString(7, 43, I18n.format("container.inventory"));
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
