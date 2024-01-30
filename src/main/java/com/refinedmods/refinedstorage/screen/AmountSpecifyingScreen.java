package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.RenderSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

public abstract class AmountSpecifyingScreen<T extends AbstractContainerMenu> extends BaseScreen<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/amount_specifying.png");

    private final BaseScreen<T> parent;

    protected EditBox amountField;
    protected Button okButton;
    protected Button cancelButton;

    protected AmountSpecifyingScreen(BaseScreen<T> parent, T container, int width, int height, Inventory playerInventory, Component title) {
        super(container, width, height, playerInventory, title);

        this.parent = parent;
    }

    protected abstract Component getOkButtonText();

    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

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

    protected int getOkCancelButtonWidth() {
        return 50;
    }

    @Override
    public void onPostInit(int x, int y) {
        Pair<Integer, Integer> pos = getOkCancelPos();

        okButton = addButton(x + pos.getLeft(), y + pos.getRight(), getOkCancelButtonWidth(), 20, getOkButtonText(), true, true, btn -> onOkButtonPressed(hasShiftDown()));
        cancelButton = addButton(x + pos.getLeft(), y + pos.getRight() + 24, getOkCancelButtonWidth(), 20, Component.translatable("gui.cancel"), true, true, btn -> close());

        amountField = new EditBox(font, x + getAmountPos().getLeft(), y + getAmountPos().getRight(), 69 - 6, font.lineHeight, Component.literal(""));
        amountField.setBordered(false);
        amountField.setVisible(true);
        amountField.setValue(String.valueOf(getDefaultAmount()));
        amountField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);
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

            Component text = Component.literal("+" + increment);
            if (text.getString().equals("+1000")) {
                text = Component.literal("+1B");
            }

            addButton(x + xx, y + 20, width, 20, text, true, true, btn -> onIncrementButtonClicked(increment));

            xx += width + 3;
        }

        xx = 7;

        for (int i = 0; i < 3; ++i) {
            int increment = increments[i];

            Component text = Component.literal("-" + increment);
            if (text.getString().equals("-1000")) {
                text = Component.literal("-1B");
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
        int oldAmount = 0;

        try {
            oldAmount = Integer.parseInt(amountField.getValue());
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

        amountField.setValue(String.valueOf(newAmount));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(getTexture(), x, y, 0, 0, imageWidth, imageHeight);
        amountField.render(graphics, 0, 0, 0);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
    }

    protected void onOkButtonPressed(boolean shiftDown) {
        // NO OP
    }

    @Override
    public boolean mouseScrolled(double x, double y, double z, double delta) {
        if (delta > 0) {
            onIncrementButtonClicked(1);
        } else {
            onIncrementButtonClicked(-1);
        }

        return super.mouseScrolled(x, y, z, delta);
    }

    public void close() {
        minecraft.setScreen(parent);
    }

    public BaseScreen<T> getParent() {
        return parent;
    }
}
