package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CraftingMonitorElementError implements ICraftingMonitorElement {
    public static final String ID = "error";

    private ICraftingMonitorElement base;
    private String tooltip;

    public CraftingMonitorElementError(ICraftingMonitorElement base, String tooltip) {
        this.base = base;
        this.tooltip = tooltip;
    }

    @Override
    public void draw(int x, int y, IElementDrawers drawers) {
        drawers.getRedOverlayDrawer().draw(x, y, null);

        base.draw(x, y, drawers);
    }

    @Override
    public boolean canDrawSelection() {
        return false;
    }

    @Override
    public int getTaskId() {
        return base.getTaskId();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, base.getId());
        ByteBufUtils.writeUTF8String(buf, tooltip);

        base.write(buf);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        return element.getId().equals(getId()) && elementHashCode() == element.elementHashCode() && base.merge(((CraftingMonitorElementError)element).base);
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ tooltip.hashCode();
    }
}
