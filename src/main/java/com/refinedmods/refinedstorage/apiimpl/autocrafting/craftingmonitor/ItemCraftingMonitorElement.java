package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCraftingMonitorElement implements ICraftingMonitorElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");
    private static final int COLOR_PROCESSING = 0xFFD9EDF7;
    private static final int COLOR_MISSING = 0xFFF2DEDE;
    private static final int COLOR_SCHEDULED = 0xFFE8E5CA;
    private static final int COLOR_CRAFTING = 0xFFADDBC6;
    private final ItemStack stack;
    private int stored;
    private int missing;
    private int processing;
    private int scheduled;
    private int crafting;

    public ItemCraftingMonitorElement(ItemStack stack, int stored, int missing, int processing, int scheduled, int crafting) {
        this.stack = stack;
        this.stored = stored;
        this.missing = missing;
        this.processing = processing;
        this.scheduled = scheduled;
        this.crafting = crafting;
    }

    public static ItemCraftingMonitorElement read(FriendlyByteBuf buf) {
        return new ItemCraftingMonitorElement(
            StackUtils.readItemStack(buf),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt()
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack matrixStack, int x, int y, IElementDrawers drawers) {
        if (missing > 0) {
            drawers.getOverlayDrawer().draw(matrixStack, x, y, COLOR_MISSING);
        } else if (processing > 0) {
            drawers.getOverlayDrawer().draw(matrixStack, x, y, COLOR_PROCESSING);
        } else if (scheduled > 0) {
            drawers.getOverlayDrawer().draw(matrixStack, x, y, COLOR_SCHEDULED);
        } else if (crafting > 0) {
            drawers.getOverlayDrawer().draw(matrixStack, x, y, COLOR_CRAFTING);
        }

        drawers.getItemDrawer().draw(matrixStack, x + 4, y + 6, stack);

        float scale = Minecraft.getInstance().isEnforceUnicode() ? 1F : 0.5F;

        matrixStack.pushPose();
        matrixStack.scale(scale, scale, 1);

        int yy = y + 7;

        if (stored > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.stored", stored));

            yy += 7;
        }

        if (missing > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.missing", missing));

            yy += 7;
        }

        if (processing > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.processing", processing));

            yy += 7;
        }

        if (scheduled > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.scheduled", scheduled));

            yy += 7;
        }

        if (crafting > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.get("gui.refinedstorage.crafting_monitor.crafting", crafting));
        }

        matrixStack.popPose();
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
        return RenderUtils.getTooltipFromItem(this.stack);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        StackUtils.writeItemStack(buf, stack);
        buf.writeInt(stored);
        buf.writeInt(missing);
        buf.writeInt(processing);
        buf.writeInt(scheduled);
        buf.writeInt(crafting);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        if (element.getId().equals(getId()) && elementHashCode() == element.elementHashCode()) {
            this.stored += ((ItemCraftingMonitorElement) element).stored;
            this.missing += ((ItemCraftingMonitorElement) element).missing;
            this.processing += ((ItemCraftingMonitorElement) element).processing;
            this.scheduled += ((ItemCraftingMonitorElement) element).scheduled;
            this.crafting += ((ItemCraftingMonitorElement) element).crafting;

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
        return API.instance().getItemStackHashCode(stack);
    }

    public static class Builder {
        private final ItemStack stack;
        private int stored;
        private int missing;
        private int processing;
        private int scheduled;
        private int crafting;

        public Builder(ItemStack stack) {
            this.stack = stack;
        }

        public static Builder forStack(ItemStack stack) {
            return new Builder(stack);
        }

        public Builder stored(int stored) {
            this.stored = stored;
            return this;
        }

        public Builder missing(int missing) {
            this.missing = missing;
            return this;
        }

        public Builder processing(int processing) {
            this.processing = processing;
            return this;
        }

        public Builder scheduled(int scheduled) {
            this.scheduled = scheduled;
            return this;
        }

        public Builder crafting(int crafting) {
            this.crafting = crafting;
            return this;
        }

        public ItemCraftingMonitorElement build() {
            return new ItemCraftingMonitorElement(stack, stored, missing, processing, scheduled, crafting);
        }
    }
}
