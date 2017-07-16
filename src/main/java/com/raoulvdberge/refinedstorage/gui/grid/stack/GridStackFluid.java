package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class GridStackFluid implements IGridStack {
    private int hash;
    private FluidStack stack;

    public GridStackFluid(Pair<Integer, FluidStack> data) {
        this.hash = data.getLeft();
        this.stack = data.getRight();
    }

    public FluidStack getStack() {
        return stack;
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public String getName() {
        return stack.getFluid().getLocalizedName(stack);
    }

    @Override
    public String getModId() {
        return stack.getFluid().getStill(stack).getResourceDomain();
    }

    @Override
    public String[] getOreIds() {
        return new String[]{stack.getFluid().getName()};
    }

    @Override
    public String getTooltip() {
        return stack.getFluid().getLocalizedName(stack);
    }

    @Override
    public int getQuantity() {
        return stack.amount;
    }

    @Override
    public void draw(GuiBase gui, int x, int y, boolean isOverWithShift) {
        GuiBase.FLUID_RENDERER.draw(gui.mc, x, y, stack);

        gui.drawQuantity(x, y, RenderUtils.formatQuantity((int) ((float) stack.amount / 1000F)));
    }

    @Override
    public Object getIngredient() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GridStackFluid && ((GridStackFluid) obj).getStack().isFluidEqual(stack);
    }
}
