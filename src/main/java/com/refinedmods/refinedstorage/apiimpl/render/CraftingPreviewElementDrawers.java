package com.refinedmods.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.screen.grid.CraftingPreviewScreen;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CraftingPreviewElementDrawers extends ElementDrawers<AbstractContainerMenu> {
    private final IElementDrawer<Integer> overlayDrawer = (poseStack, x, y, color) -> {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GuiComponent.fill(poseStack, x, y, x + 73, y + 29, color);
    };

    public CraftingPreviewElementDrawers(CraftingPreviewScreen screen) {
        super(screen);
    }

    @Override
    public IElementDrawer<Integer> getOverlayDrawer() {
        return overlayDrawer;
    }
}
