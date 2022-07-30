package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.util.EquationEvaluator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

public abstract class AmountSpecifyingScreen<T extends AbstractContainerMenu> extends BaseScreen<T> {
    private final BaseScreen<T> parent;

    protected EditBox amountField;
    protected Button okButton;
    protected Button cancelButton;

    protected AmountSpecifyingScreen(BaseScreen<T> parent, T container, int width, int height, Inventory playerInventory, Component title) {
        super(container, width, height, playerInventory, title);

        this.parent = parent;
    }

    protected abstract Component getOkButtonText();

    protected abstract String getTexture();

    protected abstract int[] getIncrements();

    protected abstract int getDefaultAmount();

    protected abstract boolean canAmountGoNegative();

    protected abstract int getMaxAmount();

    protected int getMinAmount() {
        if (canAmountGoNegative()) {
            return -getMaxAmount();
        }
        return 1;
    }

    protected int parseAmount() {
        return (int) Math.ceil(EquationEvaluator.evaluate(amountField.getText()));
    }

    protected int clampAmount(int amount) {
        return Math.max(getMinAmount(), Math.min(getMaxAmount(), amount));
    }

    protected boolean isAmountInBounds(int amount) {
        return getMinAmount() <= amount && amount <= getMaxAmount();
    }

    protected Pair<Integer, Integer> getAmountPos() {
        return Pair.of(7 + 2, 50 + 1);
    }

    protected Pair<Integer, Integer> getOkCancelPos() {
        return Pair.of(114, 33);
    }

    protected int getOkCancelButtonWidth() {
        return 50;
    }

    @Override
    public void onPostInit(int x, int y) {
        Pair<Integer, Integer> pos = getOkCancelPos();

        okButton = addButton(x + pos.getLeft(), y + pos.getRight(), getOkCancelButtonWidth(), 20, getOkButtonText(), true, true, btn -> onOkButtonPressed(hasShiftDown()));
        cancelButton = addButton(x + pos.getLeft(), y + pos.getRight() + 24, getOkCancelButtonWidth(), 20, new TranslatableComponent("gui.cancel"), true, true, btn -> close());

        amountField = new EditBox(font, x + getAmountPos().getLeft(), y + getAmountPos().getRight(), 69 - 6, font.lineHeight, new TextComponent(""));
        amountField.setBordered(false);
        amountField.setVisible(true);
        amountField.setValue(String.valueOf(getDefaultAmount()));
        amountField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        amountField.setCanLoseFocus(false);
        amountField.changeFocus(true);
        amountField.setResponder(text -> {
            int amount = 0;
            try {
                amount = Integer.parseInt(amountField.getValue());
            } catch (NumberFormatException e) {
                // NO OP
            }

            if (amount > getMaxAmount()) {
                amountField.setValue(String.valueOf(getMaxAmount()));
            }
        });

        addRenderableWidget(amountField);

        setFocused(amountField);

        int[] increments = getIncrements();

        int xx = 7;
        int width = 30;

        for (int i = 0; i < 3; ++i) {
            int increment = increments[i];

            Component text = new TextComponent("+" + increment);
            if (text.getString().equals("+1000")) {
                text = new TextComponent("+1B");
            }

            addButton(x + xx, y + 20, width, 20, text, true, true, btn -> onIncrementButtonClicked(increment));

            xx += width + 3;
        }

        xx = 7;

        for (int i = 0; i < 3; ++i) {
            int increment = increments[i];

            Component text = new TextComponent("-" + increment);
            if (text.getString().equals("-1000")) {
                text = new TextComponent("-1B");
            }

            addButton(x + xx, y + imageHeight - 20 - 7, width, 20, text, true, true, btn -> onIncrementButtonClicked(-increment));

            xx += width + 3;
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();

            return true;
        }

        if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && amountField.isFocused()) {
            onOkButtonPressed(hasShiftDown());

            return true;
        }

        if (amountField.keyPressed(key, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private void onIncrementButtonClicked(int increment) {
        try {
            int oldAmount = parseAmount();
            int newAmount = oldAmount + increment;
            if (!canAmountGoNegative() && oldAmount == 1) {
                newAmount--;
            }
            amountField.setText(String.valueOf(clampAmount(newAmount)));
        } catch (IllegalArgumentException e) {
            // NO OP
        }
    }

    private void onOkButtonPressed(boolean shiftDown) {
        try {
            int amount = parseAmount();
            if (isAmountInBounds(amount)) {
                onValidAmountSave(shiftDown, amount);
                close();
            }
        } catch (IllegalArgumentException e) {
            // NO OP
        }
    }

    protected void onValidAmountSave(boolean shiftDown, int amount) {}

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, getTexture());

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

        amountField.renderButton(poseStack, 0, 0, 0);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
    }


    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta > 0) {
            onIncrementButtonClicked(1);
        } else {
            onIncrementButtonClicked(-1);
        }

        return super.mouseScrolled(x, y, delta);
    }

    public void close() {
        minecraft.setScreen(parent);
    }

    public BaseScreen<T> getParent() {
        return parent;
    }
}
