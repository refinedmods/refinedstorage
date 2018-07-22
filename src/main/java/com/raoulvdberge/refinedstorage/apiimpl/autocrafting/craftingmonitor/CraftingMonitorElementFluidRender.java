package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CraftingMonitorElementFluidRender implements ICraftingMonitorElement {
    public static final String ID = "fluid_render";

    private FluidStack stack;
    private int quantity;
    private int offset;

    public CraftingMonitorElementFluidRender(FluidStack stack, int quantity, int offset) {
        this.stack = stack;
        this.quantity = quantity;
        this.offset = offset;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers, boolean selected) {
        if (selected) {
            drawers.getOverlayDrawer().draw(x, y, 0xFFCCCCCC);
        }

        drawers.getFluidDrawer().draw(x + 2 + offset, y + 1, stack);

        float scale = drawers.getFontRenderer().getUnicodeFlag() ? 1F : 0.5F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 21 + offset, scale), RenderUtils.getOffsetOnScale(y + 7, scale), API.instance().getQuantityFormatter().formatInBucketForm(quantity) + " " + stack.getLocalizedName());

        GlStateManager.popMatrix();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        StackUtils.writeFluidStack(buf, stack);
        buf.writeInt(quantity);
        buf.writeInt(offset);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        if (element.getId().equals(getId()) && elementHashCode() == element.elementHashCode()) {
            this.quantity += ((CraftingMonitorElementFluidRender) element).quantity;

            return true;
        }

        return false;
    }

    @Override
    public int elementHashCode() {
        return API.instance().getFluidStackHashCode(stack);
    }
}
