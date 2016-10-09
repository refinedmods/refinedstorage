package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementFluidRender implements ICraftingMonitorElement<GuiBase> {
    public static final String ID = "fluid_render";

    private int taskId;
    private FluidStack stack;
    private int offset;

    public CraftingMonitorElementFluidRender(int taskId, FluidStack stack, int offset) {
        this.taskId = taskId;
        this.stack = stack;
        this.offset = offset;
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        GuiBase.FLUID_RENDERER.draw(gui.mc, x + 2 + offset, y + 1, stack);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        gui.drawString(gui.calculateOffsetOnScale(x + 21 + offset, scale), gui.calculateOffsetOnScale(y + 7, scale), RSUtils.formatFluidStackQuantity(stack) + " " + stack.getLocalizedName());

        GlStateManager.popMatrix();
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(taskId);
        RSUtils.writeFluidStack(buf, stack);
        buf.writeInt(offset);
    }
}
