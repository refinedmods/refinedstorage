package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class CraftingMonitorElementItemRender implements ICraftingMonitorElement {
    private static final int COLOR_PROCESSING = 0xFFD9EDF7;
    private static final int COLOR_MISSING = 0xFFF2DEDE;
    private static final int COLOR_SCHEDULED = 0xFFE8E5CA;
    private static final int COLOR_CRAFTING = 0xFFADDBC6;

    public static final String ID = "item_render";

    private ItemStack stack;
    private int stored;
    private int missing;
    private int processing;
    private int scheduled;
    private int crafting;

    public CraftingMonitorElementItemRender(ItemStack stack, int stored, int missing, int processing, int scheduled, int crafting) {
        this.stack = stack;
        this.stored = stored;
        this.missing = missing;
        this.processing = processing;
        this.scheduled = scheduled;
        this.crafting = crafting;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        if (missing > 0) {
            drawers.getOverlayDrawer().draw(x, y, COLOR_MISSING);
        } else if (processing > 0) {
            drawers.getOverlayDrawer().draw(x, y, COLOR_PROCESSING);
        } else if (scheduled > 0) {
            drawers.getOverlayDrawer().draw(x, y, COLOR_SCHEDULED);
        } else if (crafting > 0) {
            drawers.getOverlayDrawer().draw(x, y, COLOR_CRAFTING);
        }

        drawers.getItemDrawer().draw(x + 4, y + 6, stack);

        float scale = drawers.getFontRenderer().getUnicodeFlag() ? 1F : 0.5F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        int yy = y + 7;

        if (stored > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.format("gui.refinedstorage:crafting_monitor.stored", stored));

            yy += 7;
        }

        if (missing > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.format("gui.refinedstorage:crafting_monitor.missing", missing));

            yy += 7;
        }

        if (processing > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.format("gui.refinedstorage:crafting_monitor.processing", processing));

            yy += 7;
        }

        if (scheduled > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.format("gui.refinedstorage:crafting_monitor.scheduled", scheduled));

            yy += 7;
        }

        if (crafting > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.format("gui.refinedstorage:crafting_monitor.crafting", crafting));
        }

        GlStateManager.popMatrix();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nullable
    @Override
    public String getTooltip() {
        return RenderUtils.getItemTooltip(this.stack).stream().collect(Collectors.joining("\n"));
    }

    @Override
    public void write(ByteBuf buf) {
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
            this.stored += ((CraftingMonitorElementItemRender) element).stored;
            this.missing += ((CraftingMonitorElementItemRender) element).missing;
            this.processing += ((CraftingMonitorElementItemRender) element).processing;
            this.scheduled += ((CraftingMonitorElementItemRender) element).scheduled;
            this.crafting += ((CraftingMonitorElementItemRender) element).crafting;

            return true;
        }

        return false;
    }

    @Override
    public int elementHashCode() {
        return API.instance().getItemStackHashCode(stack);
    }
}
