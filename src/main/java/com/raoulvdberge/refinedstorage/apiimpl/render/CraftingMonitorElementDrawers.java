package com.raoulvdberge.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class CraftingMonitorElementDrawers extends ElementDrawers {
    private int itemWidth;
    private int itemHeight;

    private IElementDrawer<Integer> overlayDrawer = (x, y, color) -> {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableLighting();

        AbstractGui.fill(x, y, x + itemWidth, y + itemHeight, color);
    };

    private IElementDrawer errorDrawer = (x, y, nothing) -> {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableLighting();

        screen.bindTexture(RS.ID, "gui/crafting_preview.png");
        screen.blit(x + itemWidth - 12 - 2, y + itemHeight - 12 - 2, 0, 244, 12, 12);
    };

    public CraftingMonitorElementDrawers(BaseScreen gui, FontRenderer fontRenderer, int itemWidth, int itemHeight) {
        super(gui, fontRenderer);

        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }

    @Override
    public IElementDrawer getErrorDrawer() {
        return errorDrawer;
    }
}
