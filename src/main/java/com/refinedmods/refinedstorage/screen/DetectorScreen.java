package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DetectorContainer;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.DetectorModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.tile.DetectorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class DetectorScreen extends BaseScreen<DetectorContainer> {
    private TextFieldWidget amountField;

    public DetectorScreen(DetectorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new TypeSideButton(this, DetectorTile.TYPE));

        addSideButton(new DetectorModeSideButton(this));

        addSideButton(new ExactModeSideButton(this, DetectorTile.COMPARE));

        amountField = new TextFieldWidget(font, x + 41 + 1, y + 23 + 1, 50, font.lineHeight, new StringTextComponent(""));
        amountField.setValue(String.valueOf(DetectorTile.AMOUNT.getValue()));
        amountField.setBordered(false);
        amountField.setVisible(true);
        amountField.setCanLoseFocus(true);
        amountField.setFocus(false);
        amountField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        amountField.setResponder(value -> {
            try {
                int result = Integer.parseInt(value);

                TileDataManager.setParameter(DetectorTile.AMOUNT, result);
            } catch (NumberFormatException e) {
                // NO OP
            }
        });

        addButton(amountField);
    }

    public void updateAmountField(int amount) {
        amountField.setValue(String.valueOf(amount));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/detector.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.get("container.inventory"));
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();

            return true;
        }

        if (amountField.keyPressed(key, scanCode, modifiers) || amountField.canConsumeInput()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }
}
