package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class OpenNetworkItemMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "open_network_item");

    private final PlayerSlot slot;

    public OpenNetworkItemMessage(PlayerSlot slot) {
        this.slot = slot;
    }

    public static OpenNetworkItemMessage decode(FriendlyByteBuf buf) {
        return new OpenNetworkItemMessage(new PlayerSlot(buf));
    }

    public static void handle(OpenNetworkItemMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            ItemStack stack = message.slot.getStackFromSlot(player);

            if (stack == null) {
                return;
            }

            if (stack.getItem() instanceof NetworkItem) {
                ((NetworkItem) stack.getItem()).applyNetwork(player.getServer(), stack,
                    n -> n.getNetworkItemManager().open(player, stack, message.slot), player::sendSystemMessage);
            } else if (stack.getItem() instanceof PortableGridBlockItem) {
                API.instance().getGridManager()
                    .openGrid(PortableGridGridFactory.ID, (ServerPlayer) player, stack, message.slot);
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        slot.writePlayerSlot(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
