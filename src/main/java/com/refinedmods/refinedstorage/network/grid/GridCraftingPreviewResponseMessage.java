package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.network.ClientProxy;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class GridCraftingPreviewResponseMessage {
    private final ResourceLocation factoryId;
    private final List<ICraftingPreviewElement<?>> elements;
    private final UUID id;
    private final int quantity;
    private final boolean fluids;

    public GridCraftingPreviewResponseMessage(ResourceLocation factoryId, List<ICraftingPreviewElement<?>> elements, UUID id, int quantity, boolean fluids) {
        this.factoryId = factoryId;
        this.elements = elements;
        this.id = id;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    public ResourceLocation getFactoryId() {
        return factoryId;
    }

    public List<ICraftingPreviewElement<?>> getElements() {
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

    public static GridCraftingPreviewResponseMessage decode(PacketBuffer buf) {
        ResourceLocation factoryId = buf.readResourceLocation();
        UUID id = buf.readUniqueId();
        int quantity = buf.readInt();
        boolean fluids = buf.readBoolean();

        List<ICraftingPreviewElement<?>> stacks = new LinkedList<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            ResourceLocation type = buf.readResourceLocation();
            stacks.add(API.instance().getCraftingPreviewElementRegistry().get(type).apply(buf));
        }

        return new GridCraftingPreviewResponseMessage(factoryId, stacks, id, quantity, fluids);
    }

    public static void encode(GridCraftingPreviewResponseMessage message, PacketBuffer buf) {
        buf.writeResourceLocation(message.factoryId);
        buf.writeUniqueId(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.fluids);
        buf.writeInt(message.elements.size());

        for (ICraftingPreviewElement<?> stack : message.elements) {
            buf.writeResourceLocation(stack.getId());
            stack.write(buf);
        }
    }

    public static void handle(GridCraftingPreviewResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientProxy.onReceivedCraftingPreviewResponseMessage(message));
        ctx.get().setPacketHandled(true);
    }
}