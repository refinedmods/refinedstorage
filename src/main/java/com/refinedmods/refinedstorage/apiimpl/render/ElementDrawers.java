package com.refinedmods.refinedstorage.apiimpl.render;

import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class ElementDrawers<T extends AbstractContainerMenu> implements IElementDrawers {
    protected final BaseScreen<T> screen;

    public ElementDrawers(BaseScreen<T> screen) {
        this.screen = screen;
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
}
