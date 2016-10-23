package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CraftingMonitorElementFluidRender implements ICraftingMonitorElement {
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
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        drawers.getFluidDrawer().draw(x + 2 + offset, y + 1, stack);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        drawers.getStringDrawer().draw(GuiBase.calculateOffsetOnScale(x + 21 + offset, scale), GuiBase.calculateOffsetOnScale(y + 7, scale), RSUtils.QUANTITY_FORMATTER.format((float) stack.amount / 1000F) + "x " + stack.getLocalizedName());

        GlStateManager.popMatrix();
    }

    @Override
    public boolean canDrawSelection() {
        return true;
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

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        if (element.getId().equals(getId()) && elementHashCode() == element.elementHashCode()) {
            this.stack.amount += ((CraftingMonitorElementFluidRender) element).stack.amount;

            return true;
        }

        return false;
    }

    @Override
    public int elementHashCode() {
        return API.instance().getFluidStackHashCode(stack);
    }
}
