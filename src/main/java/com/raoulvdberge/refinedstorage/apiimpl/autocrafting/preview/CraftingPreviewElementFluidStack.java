package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CraftingPreviewElementFluidStack implements ICraftingPreviewElement<FluidStack> {
    public static final String ID = "fluid_renderer";

    private FluidStack stack;
    private int available;
    private boolean missing;
    // If missing is true then toCraft is the missing amount
    private int toCraft;

    public CraftingPreviewElementFluidStack(FluidStack stack) {
        this.stack = stack.copy();
    }

    public CraftingPreviewElementFluidStack(FluidStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack.copy();
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    @Override
    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack));
        ByteBufUtils.writeTag(buf, stack.tag);
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public static CraftingPreviewElementFluidStack fromByteBuf(ByteBuf buf) {
        Fluid fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new CraftingPreviewElementFluidStack(new FluidStack(fluid, 1, tag), available, missing, toCraft);
    }

    @Override
    public FluidStack getElement() {
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        if (missing) {
            drawers.getOverlayDrawer().draw(x, y, 0xFFF2DEDE);
        }

        x += 5;
        y += 7;

        drawers.getFluidDrawer().draw(x, y, getElement());

        float scale = drawers.getFontRenderer().getUnicodeFlag() ? 1F : 0.5F;

        y += 2;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        if (getToCraft() > 0) {
            String format = hasMissing() ? "gui.refinedstorage:crafting_preview.missing" : "gui.refinedstorage:crafting_preview.to_craft";
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), GuiBase.t(format, API.instance().getQuantityFormatter().formatInBucketForm(getToCraft())));

            y += 7;
        }

        if (getAvailable() > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), GuiBase.t("gui.refinedstorage:crafting_preview.available", API.instance().getQuantityFormatter().formatInBucketForm(getAvailable())));
        }

        GlStateManager.popMatrix();
    }

    public void addAvailable(int amount) {
        this.available += amount;
    }

    @Override
    public int getAvailable() {
        return available;
    }

    public void addToCraft(int amount) {
        this.toCraft += amount;
    }

    @Override
    public int getToCraft() {
        return this.toCraft;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public boolean hasMissing() {
        return missing;
    }

    @Override
    public String getId() {
        return ID;
    }
}
