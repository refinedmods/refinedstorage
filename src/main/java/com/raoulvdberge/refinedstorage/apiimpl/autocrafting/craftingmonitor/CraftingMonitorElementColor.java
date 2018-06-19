package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;

public class CraftingMonitorElementColor implements ICraftingMonitorElement {
    public static final int COLOR_INFO = 0xFFD9EDF7;
    public static final int COLOR_ERROR = 0xFFF2DEDE;
    public static final int COLOR_SUCCESS = 0XFFADDBC6;

    public static final String ID = "color";

    private ICraftingMonitorElement base;
    private String tooltip;
    private int color;
    private int darkenedColor;

    public CraftingMonitorElementColor(ICraftingMonitorElement base, @Nullable String tooltip, int color) {
        this.base = base;
        this.tooltip = tooltip;
        this.color = color;

        float ratio = 1.0F - 0.1F;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);

        this.darkenedColor = (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void draw(int x, int y, IElementDrawers drawers, boolean selected) {
        drawers.getOverlayDrawer().draw(x, y, selected ? darkenedColor : color);

        base.draw(x, y, drawers, false);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    @Nullable
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(color);
        ByteBufUtils.writeUTF8String(buf, base.getId());
        ByteBufUtils.writeUTF8String(buf, tooltip);

        base.write(buf);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        return element.getId().equals(getId()) && elementHashCode() == element.elementHashCode() && base.merge(((CraftingMonitorElementColor) element).base);
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ tooltip.hashCode() ^ color;
    }
}
