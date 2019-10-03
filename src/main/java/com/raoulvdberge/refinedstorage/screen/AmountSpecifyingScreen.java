package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

public abstract class AmountSpecifyingScreen<T extends Container> extends BaseScreen<T> {
    private BaseScreen parent;

    protected TextFieldWidget amountField;
    protected Button okButton;

    public AmountSpecifyingScreen(BaseScreen parent, T container, int width, int height, PlayerInventory playerInventory, ITextComponent title) {
        super(container, width, height, playerInventory, title);

        this.parent = parent;
    }

    protected abstract String getOkButtonText();

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

        okButton = addButton(x + pos.getLeft(), y + pos.getRight(), 50, 20, getOkButtonText(), true, true, btn -> onOkButtonPressed(hasShiftDown()));
        addButton(x + pos.getLeft(), y + pos.getRight() + 24, 50, 20, I18n.format("gui.cancel"), true, true, btn -> close());

        amountField = new TextFieldWidget(font, x + getAmountPos().getLeft(), y + getAmountPos().getRight(), 69 - 6, font.FONT_HEIGHT, "");
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText(String.valueOf(getDefaultAmount()));
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.changeFocus(true);

        addButton(amountField);

        setFocused(amountField);

        int[] increments = getIncrements();

        int xx = 7;
        int width = 30;

        for (int i = 0; i < 3; ++i) {
            int increment = increments[i];

            String text = "+" + increment;

            if (text.equals("+1000")) {
                text = "+1B";
            }

            addButton(x + xx, y + 20, width, 20, text, true, true, btn -> onIncrementButtonClicked(increment));

            xx += width + 3;
        }

        xx = 7;

        for (int i = 0; i < 3; ++i) {
            int increment = increments[i];

            String text = "-" + increment;

            if (text.equals("-1000")) {
                text = "-1B";
            }

            addButton(x + xx, y + ySize - 20 - 7, width, 20, text, true, true, btn -> onIncrementButtonClicked(-increment));

            xx += width + 3;
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();

            return true;
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            onOkButtonPressed(hasShiftDown());

            return true;
        }

        if (amountField.keyPressed(key, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private void onIncrementButtonClicked(int increment) {
        int oldAmount = 0;

        try {
            oldAmount = Integer.parseInt(amountField.getText());
        } catch (NumberFormatException e) {
            // NO OP
        }

        int newAmount = increment;

        if (!canAmountGoNegative()) {
            newAmount = Math.max(1, ((oldAmount == 1 && newAmount != 1) ? 0 : oldAmount) + newAmount);
        } else {
            newAmount = oldAmount + newAmount;
        }

        if (newAmount > getMaxAmount()) {
            newAmount = getMaxAmount();
        }

        amountField.setText(String.valueOf(newAmount));
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
        renderString(7, 7, title.getFormattedText());
    }

    protected void onOkButtonPressed(boolean shiftDown) {
        // NO OP
    }

    public void close() {
        minecraft.displayGuiScreen(parent);
    }

    public BaseScreen getParent() {
        return parent;
    }
}
