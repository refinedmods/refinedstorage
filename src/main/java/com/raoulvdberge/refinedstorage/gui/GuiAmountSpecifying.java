package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import org.apache.commons.lang3.tuple.Pair;

public abstract class GuiAmountSpecifying<T extends Container> extends GuiBase<T> {
    protected TextFieldWidget amountField;

    private GuiBase parent;

    protected Button okButton;
    private Button cancelButton;

    private Button[] incrementButtons = new Button[6];

    public GuiAmountSpecifying(GuiBase parent, T container, int width, int height, PlayerInventory playerInventory) {
        super(container, width, height, playerInventory, null);

        this.parent = parent;
    }

    protected abstract String getOkButtonText();

    protected abstract String getGuiTitle();

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

        okButton = addButton(x + pos.getLeft(), y + pos.getRight(), 50, 20, getOkButtonText(), true, true, btn -> {
        });
        cancelButton = addButton(x + pos.getLeft(), y + pos.getRight() + 24, 50, 20, I18n.format("gui.cancel"), true, true, btn -> {
        });

        amountField = new TextFieldWidget(font, x + getAmountPos().getLeft(), y + getAmountPos().getRight(), 69 - 6, font.FONT_HEIGHT, "");
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText(String.valueOf(getDefaultAmount()));
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused2(true);

        int[] increments = getIncrements();

        int xx = 7;
        int width = 30;

        for (int i = 0; i < 3; ++i) {
            String text = "+" + increments[i];

            if (text.equals("+1000")) {
                text = "+1B";
            }

            incrementButtons[i] = addButton(x + xx, y + 20, width, 20, text, true, true, btn -> {
            });

            xx += width + 3;
        }

        xx = 7;

        for (int i = 0; i < 3; ++i) {
            String text = "-" + increments[i];

            if (text.equals("-1000")) {
                text = "-1B";
            }

            incrementButtons[3 + i] = addButton(x + xx, y + ySize - 20 - 7, width, 20, text, true, true, btn -> {
            });

            xx += width + 3;
        }
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, getTexture());

        blit(x, y, 0, 0, xSize, ySize);

        amountField.renderButton(0, 0, 0);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, getGuiTitle());
    }

    /* TODO
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
    }*/

    protected void onOkButtonPressed(boolean shiftDown) {
        // NO OP
    }

    public void close() {
        // TODO FMLClientHandler.instance().showGuiScreen(parent);
    }

    public GuiBase getParent() {
        return parent;
    }
}
