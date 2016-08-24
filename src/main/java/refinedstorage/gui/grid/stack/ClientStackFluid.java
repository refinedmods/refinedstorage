package refinedstorage.gui.grid.stack;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.gui.GuiBase;

import java.util.Locale;

public class ClientStackFluid implements IClientStack {
    private int hash;
    private FluidStack stack;

    public ClientStackFluid(ByteBuf buf) {
        this.hash = buf.readInt();
        this.stack = new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), ByteBufUtils.readTag(buf));
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

        gui.drawQuantity(x, y, String.format(Locale.US, "%.1f", (float) stack.amount / 1000).replace(".0", ""));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientStackFluid && ((ClientStackFluid) obj).getStack().isFluidEqual(stack);
    }
}
