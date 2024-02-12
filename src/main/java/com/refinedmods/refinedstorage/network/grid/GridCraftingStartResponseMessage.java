package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.network.ClientProxy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridCraftingStartResponseMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_crafting_start_response");

    public static void handle(GridCraftingStartResponseMessage msg, PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(ClientProxy::onReceivedCraftingStartResponseMessage);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        // no op
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
