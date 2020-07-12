package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ItemCraftingMonitorElement implements ICraftingMonitorElement {
    private static final int COLOR_PROCESSING = 0xFFD9EDF7;
    private static final int COLOR_MISSING = 0xFFF2DEDE;
    private static final int COLOR_SCHEDULED = 0xFFE8E5CA;
    private static final int COLOR_CRAFTING = 0xFFADDBC6;

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "item");

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack matrixStack, int x, int y, IElementDrawers drawers) {
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

        float scale = Minecraft.getInstance().getForceUnicodeFont() ? 1F : 0.5F;

        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, 1);

        int yy = y + 7;

        if (stored > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), new TranslationTextComponent("gui.refinedstorage.crafting_monitor.stored", stored).getString());

            yy += 7;
        }

        if (missing > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), new TranslationTextComponent("gui.refinedstorage.crafting_monitor.missing", missing).getString());

            yy += 7;
        }

        if (processing > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), new TranslationTextComponent("gui.refinedstorage.crafting_monitor.processing", processing).getString());

            yy += 7;
        }

        if (scheduled > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), new TranslationTextComponent("gui.refinedstorage.crafting_monitor.scheduled", scheduled).getString());

            yy += 7;
        }

        if (crafting > 0) {
            drawers.getStringDrawer().draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), new TranslationTextComponent("gui.refinedstorage.crafting_monitor.crafting", crafting).getString());
        }

        RenderSystem.popMatrix();
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
    public String getTooltip() {
        return String.join("\n", RenderUtils.getTooltipFromItem(this.stack));
    }

    @Override
    public void write(PacketBuffer buf) {
        StackUtils.writeItemStack(buf, stack);
        buf.writeInt(stored);
        buf.writeInt(missing);
        buf.writeInt(processing);
        buf.writeInt(scheduled);
        buf.writeInt(crafting);
    }

    public static ItemCraftingMonitorElement read(PacketBuffer buf) {
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
}
