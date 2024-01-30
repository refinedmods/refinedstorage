package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.network.ClientProxy;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridCraftingPreviewResponseMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_crafting_preview_response");

    private final List<ICraftingPreviewElement> elements;
    private final UUID id;
    private final int quantity;
    private final boolean fluids;

    public GridCraftingPreviewResponseMessage(List<ICraftingPreviewElement> elements, UUID id, int quantity,
                                              boolean fluids) {
        this.elements = elements;
        this.id = id;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    public static GridCraftingPreviewResponseMessage decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int quantity = buf.readInt();
        boolean fluids = buf.readBoolean();

        List<ICraftingPreviewElement> elements = new LinkedList<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            ResourceLocation type = buf.readResourceLocation();
            elements.add(API.instance().getCraftingPreviewElementRegistry().get(type).apply(buf));
        }

        return new GridCraftingPreviewResponseMessage(elements, id, quantity, fluids);
    }

    public static void handle(GridCraftingPreviewResponseMessage message, PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> ClientProxy.onReceivedCraftingPreviewResponseMessage(message));
    }

    public List<ICraftingPreviewElement> getElements() {
        return elements;
    }

    public UUID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isFluids() {
        return fluids;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(quantity);
        buf.writeBoolean(fluids);
        buf.writeInt(elements.size());

        for (ICraftingPreviewElement element : elements) {
            buf.writeResourceLocation(element.getId());
            element.write(buf);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
