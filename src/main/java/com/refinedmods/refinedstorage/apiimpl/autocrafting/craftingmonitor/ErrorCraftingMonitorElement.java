package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.PacketBufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ErrorCraftingMonitorElement implements ICraftingMonitorElement {
    public static final ResourceLocation ID = new ResourceLocation("error");

    private final ICraftingMonitorElement base;
    private final String message;

    public ErrorCraftingMonitorElement(ICraftingMonitorElement base, String message) {
        this.base = base;
        this.message = message;
    }

    public static ErrorCraftingMonitorElement read(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        String message = PacketBufferUtils.readString(buf);

        return new ErrorCraftingMonitorElement(
            API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf),
            message
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void draw(PoseStack poseStack, int x, int y, IElementDrawers drawers) {
        base.draw(poseStack, x, y, drawers);

        drawers.getErrorDrawer().draw(poseStack, x, y, null);
    }

    @Nullable
    @Override
    public List<Component> getTooltip() {
        List<Component> items = new ArrayList<>(base.getTooltip());
        items.add(Component.translatable(message).setStyle(Styles.RED));
        return items;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getBaseId() {
        return base.getId();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(base.getId());
        buf.writeUtf(message);

        base.write(buf);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        return elementHashCode() == element.elementHashCode() && base.merge(((ErrorCraftingMonitorElement) element).base);
    }

    @Override
    public int baseElementHashCode() {
        return base.elementHashCode();
    }

    @Override
    public int elementHashCode() {
        return base.elementHashCode() ^ message.hashCode();
    }

    public void mergeBases(ICraftingMonitorElement element) {
        this.base.merge(element);
    }
}
