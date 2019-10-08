package com.raoulvdberge.refinedstorage.screen.widget;

import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchWidget extends TextFieldWidget {
    private static final List<String> HISTORY = new ArrayList<>();

    private int mode;
    private int historyIndex = -1;

    private List<Runnable> listeners = new LinkedList<>();

    public SearchWidget(FontRenderer fontRenderer, int x, int y, int width) {
        super(fontRenderer, x, y, width, fontRenderer.FONT_HEIGHT, "");

        this.setEnableBackgroundDrawing(false);
        this.setVisible(true);
        this.setTextColor(16777215);

        /* TODO this.listeners.add(() -> {
            if (IntegrationJEI.isLoaded() && (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
                RSJEIPlugin.INSTANCE.getRuntime().getIngredientFilter().setFilterText(getText());
            }
        });*/
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean wasFocused = isFocused();

        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (flag && mouseButton == 1) {
            setText("");
            setFocused(true);

            listeners.forEach(Runnable::run);
        } else if (wasFocused != isFocused()) {
            saveHistory();
        }

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        @SuppressWarnings("deprecation") boolean canLoseFocus = ObfuscationReflectionHelper.getPrivateValue(TextFieldWidget.class, this, 6);

        boolean result = super.keyPressed(keyCode, scanCode, modifier);

        if (isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                updateSearchHistory(-1);

                result = true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                updateSearchHistory(1);

                result = true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveHistory();

                if (canLoseFocus) {
                    setFocused(false);
                }

                result = true;
            }
        }

        if (keyCode == RSKeyBindings.FOCUS_SEARCH_BAR.getKey().getKeyCode() && canLoseFocus) {
            setFocused(!isFocused());

            saveHistory();

            result = true;
        }

        if (result) {
            listeners.forEach(Runnable::run);
        }

        return result;
    }

    private void updateSearchHistory(int delta) {
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

                listeners.forEach(Runnable::run);

                return;
            }
        }

        setText(HISTORY.get(historyIndex));

        listeners.forEach(Runnable::run);
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
    }
}
