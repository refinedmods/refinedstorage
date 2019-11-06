package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.PacketBufferUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class ErrorCraftingMonitorElement implements ICraftingMonitorElement {
    public static final ResourceLocation ID = new ResourceLocation("error");

    private ICraftingMonitorElement base;
    private String message;

    public ErrorCraftingMonitorElement(ICraftingMonitorElement base, String message) {
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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeResourceLocation(base.getId());
        buf.writeString(message);

        base.write(buf);
    }

    public static ErrorCraftingMonitorElement read(PacketBuffer buf) {
        ResourceLocation id = buf.readResourceLocation();
        String message = PacketBufferUtils.readString(buf);

        return new ErrorCraftingMonitorElement(
            API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf),
            message
        );
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        return elementHashCode() == element.elementHashCode() && base.merge(((ErrorCraftingMonitorElement) element).base);
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ message.hashCode();
    }
}
