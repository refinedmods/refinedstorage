package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GridTab implements IGridTab {
    private List<IFilter> filters;
    private String name;
    @Nonnull
    private ItemStack icon;
    @Nullable
    private FluidStack fluidIcon;

    public GridTab(List<IFilter> filters, String name, @Nonnull ItemStack icon, @Nullable FluidStack fluidIcon) {
        this.filters = filters;
        this.name = name;
        this.icon = icon;
        this.fluidIcon = fluidIcon;
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public void drawTooltip(int x, int y, int screenWidth, int screenHeight, FontRenderer fontRenderer) {
        if (!name.trim().equals("")) {
            GuiUtils.drawHoveringText(Collections.singletonList(name), x, y, screenWidth, screenHeight, -1, fontRenderer);
        }
    }

    @Override
    public void drawIcon(int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer) {
        if (!icon.isEmpty()) {
            RenderSystem.setupGuiFlatDiffuseLighting();

            itemDrawer.draw(x, y, icon);
        } else {
            fluidDrawer.draw(x, y, fluidIcon);

            RenderSystem.enableAlphaTest();
        }
    }
}
