package com.refinedmods.refinedstorage.screen.widget;

import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration;
import com.refinedmods.refinedstorage.integration.jei.RSJeiPlugin;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SearchWidget extends EditBox {
    private static final List<String> HISTORY = new ArrayList<>();

    private int mode;
    private int historyIndex = -1;

    public SearchWidget(Font fontRenderer, int x, int y, int width) {
        super(fontRenderer, x, y, width, fontRenderer.lineHeight, Component.literal(""));

        this.setBordered(false);
        this.setVisible(true);
        this.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
    }

    public void updateJei() {
        if (canSyncToJEINow()) {
            RSJeiPlugin.getRuntime().getIngredientFilter().setFilterText(getValue());
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
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean clickedWidget = mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height;

        if (clickedWidget && mouseButton == 1) {
            // On right click, clear the widget and focus, save history if necessary.
            if (isFocused()) { 
                saveHistory() 
            }
            setValue("");
            setFocused(true);
        }

        if (!clickedWidget && isFocused()) {
            // If we are focused, and we click outside the search box, lose focus.
            saveHistory();
            setFocused(false);
        }

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (super.keyPressed(keyCode, scanCode, modifier)) {
            return true;
        }

        if (isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                updateHistory(-1);
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                updateHistory(1);
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveHistory();
                if (canLoseFocus) {
                    setFocused(false);
                }
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                saveHistory();
                if (!canLoseFocus) {
                    // If we can't lose focus,
                    // and we press escape,
                    // we unfocus ourselves,
                    // and close the screen immediately.
                    setFocused(false);
                    return false; // Bubble the event up to the screen.
                } else {
                    // If we can lose focus,
                    // and we press escape,
                    // we unfocus ourselves.
                    // On the next escape press, the screen will close.
                    setFocused(false);
                    return true; // Swallow
                }
            }
        }

        if (BaseScreen.isKeyDown(RSKeyBindings.FOCUS_SEARCH_BAR) && canLoseFocus) {
            setFocused(!isFocused());
            saveHistory();
            return true;
        }

        return isFocused() && canConsumeInput() && keyCode != GLFW.GLFW_KEY_ESCAPE;
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
                setValue("");

                return;
            }
        }

        setValue(HISTORY.get(historyIndex));
    }

    private void saveHistory() {
        if (!HISTORY.isEmpty() && HISTORY.get(HISTORY.size() - 1).equals(getValue())) {
            return;
        }

        if (!getValue().trim().isEmpty()) {
            HISTORY.add(getValue());
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
        if (!getValue().equals(filterText)) {
            setValue(filterText);
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (canSyncFromJEINow() && RSJeiPlugin.getRuntime().getIngredientListOverlay().hasKeyboardFocus()) {
            setTextFromJEI();
        }
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
