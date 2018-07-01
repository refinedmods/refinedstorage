package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Collections;
import java.util.List;

public class GridTab implements IGridTab {
    private List<IFilter> filters;
    private String name;
    private ItemStack icon;

    public GridTab(List<IFilter> filters, String name, ItemStack icon) {
        this.filters = filters;
        this.name = name;
        this.icon = icon;
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
    public ItemStack getIcon() {
        return icon;
    }
}
