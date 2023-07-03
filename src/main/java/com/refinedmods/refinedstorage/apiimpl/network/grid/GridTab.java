package com.refinedmods.refinedstorage.apiimpl.network.grid;

import com.mojang.blaze3d.platform.Lighting;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class GridTab implements IGridTab {
    private final List<IFilter> filters;
    @Nullable
    private final MutableComponent name;
    @Nonnull
    private final ItemStack icon;
    @Nullable
    private final FluidStack fluidIcon;

    public GridTab(List<IFilter> filters, String name, @Nonnull ItemStack icon, @Nullable FluidStack fluidIcon) {
        this.filters = filters;
        this.name = name.trim().isEmpty() ? null : Component.literal(name);
        this.icon = icon;
        this.fluidIcon = fluidIcon;
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public void drawTooltip(Font font, GuiGraphics graphics, int x, int y) {
        if (name != null) {
            graphics.renderTooltip(font, name, x, y);
        }
    }

    @Override
    public void drawIcon(GuiGraphics graphics, int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer) {
        if (!icon.isEmpty()) {
            Lighting.setupFor3DItems();
            itemDrawer.draw(graphics, x, y, icon);
        } else {
            fluidDrawer.draw(graphics, x, y, fluidIcon);
        }
    }
}
