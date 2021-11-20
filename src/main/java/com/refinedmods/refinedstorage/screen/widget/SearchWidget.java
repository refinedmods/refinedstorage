package com.refinedmods.refinedstorage.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration;
import com.refinedmods.refinedstorage.integration.jei.RSJeiPlugin;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SearchWidget extends TextFieldWidget {
    private static final List<String> HISTORY = new ArrayList<>();

    private int mode;
    private int historyIndex = -1;

    public SearchWidget(FontRenderer fontRenderer, int x, int y, int width) {
        super(fontRenderer, x, y, width, fontRenderer.FONT_HEIGHT, new StringTextComponent(""));

        this.setEnableBackgroundDrawing(false);
        this.setVisible(true);
        this.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
    }

    public void updateJei() {
        if (canSyncToJEINow()) {
            RSJeiPlugin.getRuntime().getIngredientFilter().setFilterText(getText());
        }
    }

    private boolean canSyncToJEINow() {
        return IGrid.doesSearchBoxModeUseJEI(this.mode) && JeiIntegration.isLoaded();
    }

    private boolean canSyncFromJEINow() {
        return (this.mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY ||
                this.mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED)
                && JeiIntegration.isLoaded();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean wasFocused = isFocused();

        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean clickedWidget = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (clickedWidget && mouseButton == 1) {
            setText("");
            setFocused(true);
        } else if (wasFocused != isFocused()) {
            saveHistory();
        }

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        boolean result = super.keyPressed(keyCode, scanCode, modifier);

        if (isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                updateHistory(-1);

                result = true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                updateHistory(1);

                result = true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveHistory();

                if (canLoseFocus) {
                    setFocused(false);
                }

                result = true;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                saveHistory();

                if (!canLoseFocus) {
                    // If we can't lose focus,
                    // and we press escape,
                    // we unfocus ourselves,
                    // and close the screen immediately.
                    setFocused(false);

                    result = false; // Bubble the event up to the screen.
                } else {
                    // If we can lose focus,
                    // and we press escape,
                    // we unfocus ourselves.
                    // On the next escape press, the screen will close.
                    setFocused(false);

                    result = true;
                }
            }
        }

        if (BaseScreen.isKeyDown(RSKeyBindings.FOCUS_SEARCH_BAR) && canLoseFocus) {
            setFocused(!isFocused());

            saveHistory();

            result = true;
        }

        return result;
    }

    private void updateHistory(int delta) {
        if (HISTORY.isEmpty()) {
            return;
        }

        if (historyIndex == -1) {
            historyIndex = HISTORY.size();
        }

        historyIndex += delta;

        if (historyIndex < 0) {
            historyIndex = 0;
        } else if (historyIndex > HISTORY.size() - 1) {
            historyIndex = HISTORY.size() - 1;

            if (delta == 1) {
                setText("");

                return;
            }
        }

        setText(HISTORY.get(historyIndex));
    }

    private void saveHistory() {
        if (!HISTORY.isEmpty() && HISTORY.get(HISTORY.size() - 1).equals(getText())) {
            return;
        }

        if (!getText().trim().isEmpty()) {
            HISTORY.add(getText());
        }
    }

    public void setMode(int mode) {
        this.mode = mode;

        this.setCanLoseFocus(!IGrid.isSearchBoxModeWithAutoselection(mode));
        this.setFocused(IGrid.isSearchBoxModeWithAutoselection(mode));

        if (canSyncFromJEINow()) {
            setTextFromJEI();
        }
    }

    private void setTextFromJEI() {
        final String filterText = RSJeiPlugin.getRuntime().getIngredientFilter().getFilterText();
        if (!getText().equals(filterText)) {
            setText(filterText);
        }
    }

    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (canSyncFromJEINow() && RSJeiPlugin.getRuntime().getIngredientListOverlay().hasKeyboardFocus()) {
            setTextFromJEI();
        }
        super.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
    }
}
