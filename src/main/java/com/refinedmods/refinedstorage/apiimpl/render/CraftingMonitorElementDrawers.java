package com.refinedmods.refinedstorage.apiimpl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.resources.ResourceLocation;

public class CraftingMonitorElementDrawers extends ElementDrawers<CraftingMonitorContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/crafting_preview.png");

    private final IElementDrawer<Integer> overlayDrawer;
    private final IElementDrawer<Void> errorDrawer;

    public CraftingMonitorElementDrawers(BaseScreen<CraftingMonitorContainerMenu> screen, int itemWidth, int itemHeight) {
        super(screen);

        this.overlayDrawer = (graphics, x, y, color) -> {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            graphics.fill(x, y, x + itemWidth, y + itemHeight, color);
        };

        this.errorDrawer = (graphics, x, y, nothing) -> {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            graphics.blit(TEXTURE, x + itemWidth - 12 - 2, y + itemHeight - 12 - 2, 0, 244, 12, 12);
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
