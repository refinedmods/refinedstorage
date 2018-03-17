package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.RSKeyBindings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextFieldSearch extends GuiTextField {
    private static final List<String> SEARCH_HISTORY = new ArrayList<>();

    private int searchHistoryIndex = -1;

    private List<Runnable> listeners = new LinkedList<>();

    public TextFieldSearch(int componentId, FontRenderer fontRenderer, int x, int y, int width) {
        super(componentId, fontRenderer, x, y, width, fontRenderer.FONT_HEIGHT);

        setEnableBackgroundDrawing(false);
        setVisible(true);
        setTextColor(16777215);
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        boolean canLoseFocus = ObfuscationReflectionHelper.getPrivateValue(GuiTextField.class, this, 10);

        boolean result = super.textboxKeyTyped(typedChar, keyCode);

        if (isFocused()) {
            if (keyCode == Keyboard.KEY_UP) {
                updateSearchHistory(-1);

                result = true;
            } else if (keyCode == Keyboard.KEY_DOWN) {
                updateSearchHistory(1);

                result = true;
            } else if (keyCode == Keyboard.KEY_RETURN) {
                saveHistory();

                if (canLoseFocus) {
                    setFocused(false);
                }

                result = true;
            }
        }

        if (keyCode == RSKeyBindings.FOCUS_SEARCH_BAR.getKeyCode() && canLoseFocus) {
            setFocused(!isFocused());

            saveHistory();

            result = true;
        }

        return result;
    }

    private void updateSearchHistory(int delta) {
        if (SEARCH_HISTORY.isEmpty()) {
            return;
        }

        if (searchHistoryIndex == -1) {
            searchHistoryIndex = SEARCH_HISTORY.size();
        }

        searchHistoryIndex += delta;

        if (searchHistoryIndex < 0) {
            searchHistoryIndex = 0;
        } else if (searchHistoryIndex > SEARCH_HISTORY.size() - 1) {
            searchHistoryIndex = SEARCH_HISTORY.size() - 1;

            if (delta == 1) {
                setText("");

                listeners.forEach(Runnable::run);

                return;
            }
        }

        setText(SEARCH_HISTORY.get(searchHistoryIndex));

        listeners.forEach(Runnable::run);
    }

    private void saveHistory() {
        if (!SEARCH_HISTORY.isEmpty() && SEARCH_HISTORY.get(SEARCH_HISTORY.size() - 1).equals(getText())) {
            return;
        }

        if (!getText().trim().isEmpty()) {
            SEARCH_HISTORY.add(getText());
        }
    }
}
