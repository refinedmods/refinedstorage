package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;

public class CraftingMonitorElementError implements ICraftingMonitorElement {
    public static final String ID = "error";

    private ICraftingMonitorElement base;
    private String message;

    public CraftingMonitorElementError(ICraftingMonitorElement base, String message) {
        this.base = base;
        this.message = message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void draw(int x, int y, IElementDrawers drawers) {
        base.draw(x, y, drawers);

        drawers.getErrorDrawer().draw(x, y, null);
    }

    @Nullable
    @Override
    public String getTooltip() {
        return base.getTooltip() + "\n" + TextFormatting.RED + I18n.format(message);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, base.getId());
        ByteBufUtils.writeUTF8String(buf, message);

        base.write(buf);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        return elementHashCode() == element.elementHashCode() && base.merge(((CraftingMonitorElementError) element).base);
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ message.hashCode();
    }
}
