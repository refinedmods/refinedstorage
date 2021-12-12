package com.refinedmods.refinedstorage.apiimpl.network.grid;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GridTab implements IGridTab {
    private final List<IFilter> filters;
    private final String name;
    @Nonnull
    private final ItemStack icon;
    @Nullable
    private final FluidStack fluidIcon;

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
    public void drawTooltip(MatrixStack matrixStack, int x, int y, int screenWidth, int screenHeight, FontRenderer fontRenderer) {
        if (!name.trim().equals("")) {
            GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new StringTextComponent(name)), x, y, screenWidth, screenHeight, -1, fontRenderer);
        }
    }

    @Override
    public void drawIcon(MatrixStack matrixStack, int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer) {
        if (!icon.isEmpty()) {
            RenderHelper.setupFor3DItems();

            itemDrawer.draw(matrixStack, x, y, icon);
        } else {
            fluidDrawer.draw(matrixStack, x, y, fluidIcon);

            RenderSystem.enableAlphaTest();
        }
    }
}
