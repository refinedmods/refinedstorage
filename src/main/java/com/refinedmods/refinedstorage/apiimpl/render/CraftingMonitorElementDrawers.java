package com.refinedmods.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.AbstractGui;

public class CraftingMonitorElementDrawers extends ElementDrawers<CraftingMonitorContainer> {
    private final IElementDrawer<Integer> overlayDrawer;
    private final IElementDrawer<Void> errorDrawer;

    public CraftingMonitorElementDrawers(BaseScreen<CraftingMonitorContainer> screen, int itemWidth, int itemHeight) {
        super(screen);

        this.overlayDrawer = (matrixStack, x, y, color) -> {
            RenderSystem.color4f(1, 1, 1, 1);
            RenderSystem.disableLighting();

            AbstractGui.fill(matrixStack, x, y, x + itemWidth, y + itemHeight, color);
        };

        this.errorDrawer = (matrixStack, x, y, nothing) -> {
            RenderSystem.color4f(1, 1, 1, 1);
            RenderSystem.disableLighting();

            screen.bindTexture(RS.ID, "gui/crafting_preview.png");
            screen.blit(matrixStack, x + itemWidth - 12 - 2, y + itemHeight - 12 - 2, 0, 244, 12, 12);
        };
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }

    @Override
    public IElementDrawer<Void> getErrorDrawer() {
        return errorDrawer;
    }
}
