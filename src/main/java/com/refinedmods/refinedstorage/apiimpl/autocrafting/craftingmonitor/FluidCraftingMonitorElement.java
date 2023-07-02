package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class FluidCraftingMonitorElement implements ICraftingMonitorElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid");
    private static final int COLOR_PROCESSING = 0xFFD9EDF7;
    private static final int COLOR_MISSING = 0xFFF2DEDE;
    private static final int COLOR_SCHEDULED = 0xFFE8E5CA;
    private static final int COLOR_CRAFTING = 0xFFADDBC6;
    private final FluidStack stack;
    private int stored;
    private int missing;
    private int processing;
    private int scheduled;
    private int crafting;

    public FluidCraftingMonitorElement(FluidStack stack, int stored, int missing, int processing, int scheduled, int crafting) {
        this.stack = stack;
        this.stored = stored;
        this.missing = missing;
        this.processing = processing;
        this.scheduled = scheduled;
        this.crafting = crafting;
    }

    public static FluidCraftingMonitorElement read(FriendlyByteBuf buf) {
        return new FluidCraftingMonitorElement(
            FluidStack.readFromPacket(buf),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt()
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(GuiGraphics graphics, int x, int y, IElementDrawers drawers) {
        if (missing > 0) {
            drawers.getOverlayDrawer().draw(graphics, x, y, COLOR_MISSING);
        } else if (processing > 0) {
            drawers.getOverlayDrawer().draw(graphics, x, y, COLOR_PROCESSING);
        } else if (scheduled > 0) {
            drawers.getOverlayDrawer().draw(graphics, x, y, COLOR_SCHEDULED);
        } else if (crafting > 0) {
            drawers.getOverlayDrawer().draw(graphics, x, y, COLOR_CRAFTING);
        }

        drawers.getFluidDrawer().draw(graphics, x + 4, y + 6, stack);

        float scale = Minecraft.getInstance().isEnforceUnicode() ? 1F : 0.5F;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);

        int yy = y + 7;

        if (stored > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.stored", API.instance().getQuantityFormatter().formatInBucketForm(stored)));

            yy += 7;
        }

        if (missing > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.missing", API.instance().getQuantityFormatter().formatInBucketForm(missing)));

            yy += 7;
        }

        if (processing > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.processing", API.instance().getQuantityFormatter().formatInBucketForm(processing)));

            yy += 7;
        }

        if (scheduled > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.scheduled", API.instance().getQuantityFormatter().formatInBucketForm(scheduled)));

            yy += 7;
        }

        if (crafting > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.crafting", API.instance().getQuantityFormatter().formatInBucketForm(crafting)));
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getBaseId() {
        return ID;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    @Override
    public List<Component> getTooltip() {
        return Collections.singletonList(stack.getDisplayName());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        stack.writeToPacket(buf);
        buf.writeInt(stored);
        buf.writeInt(missing);
        buf.writeInt(processing);
        buf.writeInt(scheduled);
        buf.writeInt(crafting);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        if (element.getId().equals(getId()) && elementHashCode() == element.elementHashCode()) {
            this.stored += ((FluidCraftingMonitorElement) element).stored;
            this.missing += ((FluidCraftingMonitorElement) element).missing;
            this.processing += ((FluidCraftingMonitorElement) element).processing;
            this.scheduled += ((FluidCraftingMonitorElement) element).scheduled;
            this.crafting += ((FluidCraftingMonitorElement) element).crafting;

            return true;
        }

        return false;
    }

    @Override
    public int baseElementHashCode() {
        return elementHashCode();
    }

    @Override
    public int elementHashCode() {
        return API.instance().getFluidStackHashCode(stack);
    }

    public static class Builder {
        private final FluidStack stack;
        private int stored;
        private int missing;
        private int processing;
        private int scheduled;
        private int crafting;

        public Builder(FluidStack stack) {
            this.stack = stack;
        }

        public static FluidCraftingMonitorElement.Builder forStack(FluidStack stack) {
            return new FluidCraftingMonitorElement.Builder(stack);
        }

        public FluidCraftingMonitorElement.Builder stored(int stored) {
            this.stored = stored;
            return this;
        }

        public FluidCraftingMonitorElement.Builder missing(int missing) {
            this.missing = missing;
            return this;
        }

        public FluidCraftingMonitorElement.Builder processing(int processing) {
            this.processing = processing;
            return this;
        }

        public FluidCraftingMonitorElement.Builder scheduled(int scheduled) {
            this.scheduled = scheduled;
            return this;
        }

        public FluidCraftingMonitorElement.Builder crafting(int crafting) {
            this.crafting = crafting;
            return this;
        }

        public FluidCraftingMonitorElement build() {
            return new FluidCraftingMonitorElement(stack, stored, missing, processing, scheduled, crafting);
        }
    }
}
