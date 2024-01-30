package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidCraftingPreviewElement implements ICraftingPreviewElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid");

    private final FluidStack stack;
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

    public static FluidCraftingPreviewElement read(FriendlyByteBuf buf) {
        FluidStack stack = FluidStack.readFromPacket(buf);
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new FluidCraftingPreviewElement(stack, available, missing, toCraft);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        stack.writeToPacket(buf);
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public FluidStack getStack() {
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(GuiGraphics graphics, int x, int y, IElementDrawers drawers) {
        if (missing) {
            drawers.getOverlayDrawer().draw(graphics, x, y, 0xFFF2DEDE);
        }

        x += 5;
        y += 7;

        drawers.getFluidDrawer().draw(graphics, x, y, getStack());

        float scale = Minecraft.getInstance().isEnforceUnicode() ? 1F : 0.5F;

        y += 2;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);

        if (toCraft > 0) {
            String format = doesDisableTaskStarting() ? "gui.refinedstorage.crafting_preview.missing" : "gui.refinedstorage.crafting_preview.to_craft";
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.get(format, API.instance().getQuantityFormatter().formatInBucketForm(toCraft)));

            y += 7;
        }

        if (available > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.get("gui.refinedstorage.crafting_preview.available", API.instance().getQuantityFormatter().formatInBucketForm(available)));
        }

        poseStack.popPose();
    }

    public void addAvailable(int amount) {
        this.available += amount;
    }

    public void addToCraft(int amount) {
        this.toCraft += amount;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public boolean doesDisableTaskStarting() {
        return missing;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
