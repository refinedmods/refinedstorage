package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.network.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class GridCraftingPreviewResponseMessage {
    private final List<ICraftingPreviewElement> elements;
    private final UUID id;
    private final int quantity;
    private final boolean fluids;

    public GridCraftingPreviewResponseMessage(List<ICraftingPreviewElement> elements, UUID id, int quantity, boolean fluids) {
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

    public static void encode(GridCraftingPreviewResponseMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.fluids);
        buf.writeInt(message.elements.size());

        for (ICraftingPreviewElement element : message.elements) {
            buf.writeResourceLocation(element.getId());
            element.write(buf);
        }
    }

    public static void handle(GridCraftingPreviewResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientProxy.onReceivedCraftingPreviewResponseMessage(message));
        ctx.get().setPacketHandled(true);
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
}
