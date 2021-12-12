package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenNetworkItemMessage {
    private final PlayerSlot slot;

    public OpenNetworkItemMessage(PlayerSlot slot) {
        this.slot = slot;
    }

    public static OpenNetworkItemMessage decode(PacketBuffer buf) {
        return new OpenNetworkItemMessage(new PlayerSlot(buf));
    }

    public static void encode(OpenNetworkItemMessage message, PacketBuffer buf) {
        message.slot.writePlayerSlot(buf);
    }

    public static void handle(OpenNetworkItemMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                ItemStack stack = message.slot.getStackFromSlot(player);

                if (stack == null) {
                    return;
                }

                if (stack.getItem() instanceof NetworkItem) {
                    ((NetworkItem) stack.getItem()).applyNetwork(player.getServer(), stack, n -> n.getNetworkItemManager().open(player, stack, message.slot), err -> player.sendMessage(err, player.getUUID()));
                } else if (stack.getItem() instanceof PortableGridBlockItem) {
                    API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, player, stack, message.slot);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
