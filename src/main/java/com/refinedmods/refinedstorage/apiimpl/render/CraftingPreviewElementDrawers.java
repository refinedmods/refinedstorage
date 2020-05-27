package com.refinedmods.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.screen.grid.CraftingPreviewScreen;
import net.minecraft.client.gui.FontRenderer;

public class CraftingPreviewElementDrawers extends ElementDrawers {
    private CraftingPreviewScreen screen;
    private IElementDrawer<Integer> overlayDrawer = (x, y, colour) -> {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableLighting();

        screen.fill(x, y, x + 73, y + 29, colour);
    };

    public CraftingPreviewElementDrawers(CraftingPreviewScreen screen, FontRenderer fontRenderer) {
        super(screen, fontRenderer);

        this.screen = screen;
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }
}
