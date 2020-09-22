package com.refinedmods.refinedstorage.apiimpl.render;

import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ElementDrawers implements IElementDrawers {
    protected final BaseScreen screen;
    private final FontRenderer fontRenderer;

    public ElementDrawers(BaseScreen screen, FontRenderer fontRenderer) {
        this.screen = screen;
        this.fontRenderer = fontRenderer;
    }

    @Override
    public IElementDrawer<ItemStack> getItemDrawer() {
        return screen::renderItem;
    }

    @Override
    public IElementDrawer<FluidStack> getFluidDrawer() {
        return FluidRenderer.INSTANCE::render;
    }

    @Override
    public IElementDrawer<String> getStringDrawer() {
        return screen::renderString;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
