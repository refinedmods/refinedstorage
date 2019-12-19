package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

public class FluidCraftingPreviewElement implements ICraftingPreviewElement<FluidStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid");

    private FluidStack stack;
    private int available;
    private boolean missing;
    // If missing is true then toCraft is the missing amount
    private int toCraft;

    public FluidCraftingPreviewElement(FluidStack stack) {
        this.stack = stack.copy();
    }

    public FluidCraftingPreviewElement(FluidStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack.copy();
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    @Override
    public void write(PacketBuffer buf) {
        stack.writeToPacket(buf);
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public static FluidCraftingPreviewElement read(PacketBuffer buf) {
        FluidStack stack = FluidStack.readFromPacket(buf);
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new FluidCraftingPreviewElement(stack, available, missing, toCraft);
    }

    @Override
    public FluidStack getElement() {
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        if (missing) {
            drawers.getOverlayDrawer().draw(x, y, 0xFFF2DEDE);
        }

        x += 5;
        y += 7;

        drawers.getFluidDrawer().draw(x, y, getElement());

        float scale = Minecraft.getInstance().getForceUnicodeFont() ? 1F : 0.5F;

        y += 2;

        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, 1);

        if (getToCraft() > 0) {
            String format = hasMissing() ? "gui.refinedstorage.crafting_preview.missing" : "gui.refinedstorage.crafting_preview.to_craft";
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.format(format, API.instance().getQuantityFormatter().formatInBucketForm(getToCraft())));

            y += 7;
        }

        if (getAvailable() > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.format("gui.refinedstorage.crafting_preview.available", API.instance().getQuantityFormatter().formatInBucketForm(getAvailable())));
        }

        RenderSystem.popMatrix();
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
    public ResourceLocation getId() {
        return ID;
    }
}
