package refinedstorage.gui.grid.stack;

import io.netty.buffer.ByteBuf;
import mezz.jei.gui.ingredients.FluidStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.gui.GuiBase;

import java.util.Locale;

public class ClientStackFluid implements IClientStack {
    private FluidStack stack;
    private FluidStackRenderer renderer;

    public ClientStackFluid(ByteBuf buf) {
        stack = new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), ByteBufUtils.readTag(buf));
        renderer = new FluidStackRenderer(1000, false, 16, 16, null);
    }

    public FluidStack getStack() {
        return stack;
    }

    @Override
    public String getName() {
        return stack.getFluid().getLocalizedName(stack);
    }

    // @todo: ;-)
    @Override
    public String getModId() {
        return "minecraft";
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
        renderer.draw(Minecraft.getMinecraft(), x, y, stack);

        gui.drawQuantity(x, y, String.format(Locale.US, "%.1f", (float) stack.amount / 1000).replace(".0", "") + "B");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientStackFluid && ((ClientStackFluid) obj).getStack().isFluidEqual(stack);
    }

    public static void write(ByteBuf buf, FluidStack stack) {
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
        buf.writeInt(stack.amount);
        ByteBufUtils.writeTag(buf, stack.tag);
    }
}
