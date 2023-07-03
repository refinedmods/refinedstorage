package com.refinedmods.refinedstorage.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.integration.jei.GridRecipeTransferHandler;
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.LinkedList;
import java.util.List;

public class ScrollbarWidget implements GuiEventListener {
    private static final int SCROLLER_HEIGHT = 15;

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final List<ScrollbarWidgetListener> listeners = new LinkedList<>();
    private final BaseScreen<?> screen;
    private boolean enabled = false;
    private int offset;
    private int maxOffset;
    private boolean clicked = false;
    private boolean small = false;
    private boolean focused = false;

    public ScrollbarWidget(BaseScreen<?> screen, int x, int y, int width, int height) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ScrollbarWidget(BaseScreen screen, int x, int y, int width, int height, boolean small) {
        this(screen, x, y, width, height);
        this.small = small;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void render(GuiGraphics graphics) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (small) {
            graphics.blit(BaseScreen.ICONS_TEXTURE, screen.getGuiLeft() + x, screen.getGuiTop() + y + (int) Math.min(height - SCROLLER_HEIGHT, (float) offset / (float) maxOffset * (float) (height - SCROLLER_HEIGHT)), isEnabled() ? 218 : 225, 0, 7, SCROLLER_HEIGHT);
        } else {
            graphics.blit(BaseScreen.ICONS_TEXTURE, screen.getGuiLeft() + x, screen.getGuiTop() + y + (int) Math.min(height - SCROLLER_HEIGHT, (float) offset / (float) maxOffset * (float) (height - SCROLLER_HEIGHT)), isEnabled() ? 232 : 244, 0, 12, SCROLLER_HEIGHT);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        mx -= screen.getGuiLeft();
        my -= screen.getGuiTop();

        if (button == 0 && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            // Prevent accidental scrollbar click after clicking recipe transfer button
            if (JeiIntegration.isLoaded() && GridRecipeTransferHandler.INSTANCE.hasTransferredRecently()) {
                return false;
            }

            updateOffset(my);

            clicked = true;

            return true;
        }

        return false;
    }

    @Override
    public void mouseMoved(double mx, double my) {
        mx -= screen.getGuiLeft();
        my -= screen.getGuiTop();

        if (clicked && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            updateOffset(my);
        }
    }

    private void updateOffset(double my) {
        setOffset((int) Math.floor((float) (my - y) / (float) (height - SCROLLER_HEIGHT) * (float) maxOffset));
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (clicked) {
            clicked = false;

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (isEnabled()) {
            setOffset(offset + Math.max(Math.min(-(int) scrollDelta, 1), -1));

            return true;
        }

        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
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
