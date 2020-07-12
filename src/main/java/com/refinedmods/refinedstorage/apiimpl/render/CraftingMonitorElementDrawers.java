package com.refinedmods.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class CraftingMonitorElementDrawers extends ElementDrawers {
    private int itemWidth;
    private int itemHeight;

    private final IElementDrawer<Integer> overlayDrawer = (matrixStack, x, y, color) -> {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableLighting();

        AbstractGui.fill(matrixStack, x, y, x + itemWidth, y + itemHeight, color);
    };

    private final IElementDrawer<?> errorDrawer = (matrixStack, x, y, nothing) -> {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableLighting();

        screen.bindTexture(RS.ID, "gui/crafting_preview.png");
        screen.blit(matrixStack, x + itemWidth - 12 - 2, y + itemHeight - 12 - 2, 0, 244, 12, 12);
    };

    public CraftingMonitorElementDrawers(BaseScreen<CraftingMonitorContainer> gui, FontRenderer fontRenderer, int itemWidth, int itemHeight) {
        super(gui, fontRenderer);

        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }

    @Override
    public IElementDrawer<?> getErrorDrawer() {
        return errorDrawer;
    }
}
