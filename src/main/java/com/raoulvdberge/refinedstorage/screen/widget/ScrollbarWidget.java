package com.raoulvdberge.refinedstorage.screen.widget;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import java.util.LinkedList;
import java.util.List;

public class ScrollbarWidget {
    private static final int SCROLLER_HEIGHT = 15;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean enabled = false;

    private int offset;
    private int maxOffset;

    private boolean wasClicking = false;
    private boolean isScrolling = false;

    private List<ScrollbarWidgetListener> listeners = new LinkedList<>();

    public ScrollbarWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addListener(ScrollbarWidgetListener listener) {
        listeners.add(listener);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void draw(BaseScreen gui) {
        gui.bindTexture(RS.ID, "icons.png");
        gui.blit(gui.getGuiLeft() + x, gui.getGuiTop() + y + (int) Math.min(height - SCROLLER_HEIGHT, (float) offset / (float) maxOffset * (float) (height - SCROLLER_HEIGHT)), isEnabled() ? 232 : 244, 0, 12, 15);
    }

    public void update(BaseScreen gui, int mouseX, int mouseY) {
        if (!isEnabled()) {
            isScrolling = false;
            wasClicking = false;
        } else {
            // TODO boolean down = Mouse.isButtonDown(0);
            boolean down = false;

            if (!wasClicking && down && RenderUtils.inBounds(x, y, width, height, mouseX, mouseY)) {
                isScrolling = true;
            }

            if (!down) {
                isScrolling = false;
            }

            wasClicking = down;

            if (isScrolling) {
                setOffset((int) Math.floor((float) (mouseY - y) / (float) (height - SCROLLER_HEIGHT) * (float) maxOffset));
            }
        }
    }

    public void wheel(int delta) {
        if (isEnabled()) {
            setOffset(offset + Math.max(Math.min(-delta, 1), -1));
        }
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;

        if (offset > maxOffset) {
            this.offset = Math.max(0, maxOffset);
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        int oldOffset = this.offset;

        if (offset >= 0 && offset <= maxOffset) {
            this.offset = offset;

            listeners.forEach(l -> l.onOffsetChanged(oldOffset, offset));
        }
    }
}
