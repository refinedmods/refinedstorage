package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.raoulvdberge.refinedstorage.item.NetworkItem;
import com.raoulvdberge.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenNetworkItemMessage {
    private int slotId;

    public OpenNetworkItemMessage(int slotId) {
        this.slotId = slotId;
    }

    public static OpenNetworkItemMessage decode(PacketBuffer buf) {
        return new OpenNetworkItemMessage(buf.readInt());
    }

    public static void encode(OpenNetworkItemMessage message, PacketBuffer buf) {
        buf.writeInt(message.slotId);
    }

    public static void handle(OpenNetworkItemMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                ItemStack stack = player.inventory.getStackInSlot(message.slotId);

                if (stack.getItem() instanceof NetworkItem) {
                    ((NetworkItem) stack.getItem()).applyNetwork(player.getServer(), stack, n -> n.getNetworkItemManager().open(player, stack, message.slotId), player::sendMessage);
                } else if (stack.getItem() instanceof PortableGridBlockItem) {
                    API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, player, stack, message.slotId);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
