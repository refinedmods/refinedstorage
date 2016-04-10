package refinedstorage.gui;

import net.minecraft.client.gui.GuiTextField;
import refinedstorage.container.ContainerDummy;

import java.io.IOException;

public class GuiCraftingSettings extends GuiBase {
    private GuiTextField amountField;

    public GuiCraftingSettings() {
        super(new ContainerDummy(), 143, 61);
    }

    @Override
    public void init(int x, int y) {
        addButton(x + 48, y + 35, 50, 20, t("misc.refinedstorage:start"));

        amountField = new GuiTextField(0, fontRendererObj, x + 39 + 1, y + 21 + 1, 69 - 6, fontRendererObj.FONT_HEIGHT);
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText("1");
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_settings.png");

        drawTexture(x, y, 0, 0, width, height);

        amountField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(53, 7, t("container.crafting"));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
            // NO OP
        } else {
            super.keyTyped(character, keyCode);
        }
    }
}
