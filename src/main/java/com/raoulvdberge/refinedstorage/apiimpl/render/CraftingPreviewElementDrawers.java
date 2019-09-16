package com.raoulvdberge.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingPreview;
import net.minecraft.client.gui.FontRenderer;

public class CraftingPreviewElementDrawers extends ElementDrawers {
    private GuiCraftingPreview gui;
    private IElementDrawer<Integer> overlayDrawer = (x, y, colour) -> {
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.disableLighting();

        gui.fill(x, y, x + 73, y + 29, colour);
    };

    public CraftingPreviewElementDrawers(GuiCraftingPreview gui, FontRenderer fontRenderer) {
        super(gui, fontRenderer);

        this.gui = gui;
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }
}
