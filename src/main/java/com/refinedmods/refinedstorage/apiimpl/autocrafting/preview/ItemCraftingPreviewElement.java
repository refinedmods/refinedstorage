package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemCraftingPreviewElement implements ICraftingPreviewElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");

    private final ItemStack stack;
    private int available;
    private boolean missing;
    // If missing is true then toCraft is the missing amount
    private int toCraft;

    public ItemCraftingPreviewElement(ItemStack stack) {
        this.stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
    }

    public ItemCraftingPreviewElement(ItemStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack;
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    public static ItemCraftingPreviewElement read(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new ItemCraftingPreviewElement(stack, available, missing, toCraft);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(GuiGraphics graphics, int x, int y, IElementDrawers drawers) {
        if (missing) {
            drawers.getOverlayDrawer().draw(graphics, x, y, 0xFFF2DEDE);
        }

        x += 5;
        y += 7;

        drawers.getItemDrawer().draw(graphics, x, y, stack);

        float scale = Minecraft.getInstance().isEnforceUnicode() ? 1F : 0.5F;

        y += 2;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);

        if (toCraft > 0) {
            String format = doesDisableTaskStarting() ? "gui.refinedstorage.crafting_preview.missing" : "gui.refinedstorage.crafting_preview.to_craft";
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.get(format, toCraft));

            y += 7;
        }

        if (available > 0) {
            drawers.getStringDrawer().draw(graphics, RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.get("gui.refinedstorage.crafting_preview.available", available));
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
