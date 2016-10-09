package refinedstorage.gui.grid.stack;

import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import refinedstorage.RSUtils;
import refinedstorage.gui.GuiBase;

public class ClientStackFluid implements IClientStack {
    private int hash;
    private FluidStack stack;

    public ClientStackFluid(Pair<Integer, FluidStack> data) {
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

        gui.drawQuantity(x, y, RSUtils.formatFluidStackQuantity(stack));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientStackFluid && ((ClientStackFluid) obj).getStack().isFluidEqual(stack);
    }
}
