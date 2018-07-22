package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public abstract class GuiAmountSpecifying extends GuiBase {
    protected GuiTextField amountField;

    private GuiBase parent;

    protected GuiButton okButton;
    private GuiButton cancelButton;

    private GuiButton[] incrementButtons = new GuiButton[6];

    public GuiAmountSpecifying(GuiBase parent, Container container, int width, int height) {
        super(container, width, height);

        this.parent = parent;
    }

    protected abstract String getOkButtonText();

    protected abstract String getTitle();

    protected abstract String getTexture();

    protected abstract int[] getIncrements();

    protected abstract int getDefaultAmount();

    protected abstract boolean canAmountGoNegative();

    protected abstract int getMaxAmount();

    protected Pair<Integer, Integer> getAmountPos() {
        return Pair.of(7 + 2, 50 + 1);
    }

    protected Pair<Integer, Integer> getOkCancelPos() {
        return Pair.of(114, 33);
    }

    @Override
    public void init(int x, int y) {
        Pair<Integer, Integer> pos = getOkCancelPos();

        okButton = addButton(x + pos.getLeft(), y + pos.getRight(), 50, 20, getOkButtonText());
        cancelButton = addButton(x + pos.getLeft(), y + pos.getRight() + 24, 50, 20, t("gui.cancel"));

        amountField = new GuiTextField(0, fontRenderer, x + getAmountPos().getLeft(), y + getAmountPos().getRight(), 69 - 6, fontRenderer.FONT_HEIGHT);
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText(String.valueOf(getDefaultAmount()));
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);

        int[] increments = getIncrements();

        int xx = 7;
        int width = 30;

        for (int i = 0; i < 3; ++i) {
            String text = "+" + increments[i];

            if (text.equals("+1000")) {
                text = "+1B";
            }

            incrementButtons[i] = addButton(x + xx, y + 20, width, 20, text);

            xx += width + 3;
        }

        xx = 7;

        for (int i = 0; i < 3; ++i) {
            String text = "-" + increments[i];

            if (text.equals("-1000")) {
                text = "-1B";
            }

            incrementButtons[3 + i] = addButton(x + xx, y + screenHeight - 20 - 7, width, 20, text);

            xx += width + 3;
        }
    }

    @Override
    public void update(int x, int y) {
        // NO OP
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(getTexture());

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        amountField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, getTitle());
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
            // NO OP
        } else {
            if (keyCode == Keyboard.KEY_RETURN) {
                onOkButtonPressed(isShiftKeyDown());
            } else if (keyCode == Keyboard.KEY_ESCAPE) {
                close();
            } else {
                super.keyTyped(character, keyCode);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == okButton.id) {
            onOkButtonPressed(isShiftKeyDown());
        } else if (button.id == cancelButton.id) {
            close();
        } else {
            for (GuiButton incrementButton : incrementButtons) {
                if (incrementButton.id == button.id) {
                    Integer oldAmount = Ints.tryParse(amountField.getText());

                    if (oldAmount == null) {
                        oldAmount = 0;
                    }

                    String incrementButtonText = incrementButton.displayString;

                    if (incrementButtonText.equals("+1B")) {
                        incrementButtonText = "1000";
                    } else if (incrementButtonText.equals("-1B")) {
                        incrementButtonText = "-1000";
                    }

                    int newAmount = Integer.parseInt(incrementButtonText);

                    if (!canAmountGoNegative()) {
                        newAmount = Math.max(1, ((oldAmount == 1 && newAmount != 1) ? 0 : oldAmount) + newAmount);
                    } else {
                        newAmount = oldAmount + newAmount;
                    }

                    if (newAmount > getMaxAmount()) {
                        newAmount = getMaxAmount();
                    }

                    amountField.setText(String.valueOf(newAmount));

                    break;
                }
            }
        }
    }

    protected void onOkButtonPressed(boolean shiftDown) {
        // NO OP
    }

    public void close() {
        FMLClientHandler.instance().showGuiScreen(parent);
    }

    public GuiBase getParent() {
        return parent;
    }
}
