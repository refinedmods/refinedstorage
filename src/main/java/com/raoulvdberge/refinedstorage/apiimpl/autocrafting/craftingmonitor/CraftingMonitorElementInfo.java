package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CraftingMonitorElementInfo implements ICraftingMonitorElement {
    public static final String ID = "info";

    private ICraftingMonitorElement base;
    private String tooltip;

    public CraftingMonitorElementInfo(ICraftingMonitorElement base, String tooltip) {
        this.base = base;
        this.tooltip = tooltip;
    }

    @Override
    public void draw(int x, int y, IElementDrawers drawers) {
        drawers.getOverlayDrawer().draw(x, y, 0xFFD9EDF7);

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
        return element.getId().equals(getId()) && elementHashCode() == element.elementHashCode() && base.merge(((CraftingMonitorElementInfo) element).base);
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ tooltip.hashCode();
    }
}
