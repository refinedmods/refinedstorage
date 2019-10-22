package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.item.NetworkItem;
import net.minecraft.entity.player.PlayerEntity;
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
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                ItemStack stack = player.inventory.getStackInSlot(message.slotId);

                if (stack.getItem() instanceof NetworkItem) {
                    ((NetworkItem) stack.getItem()).applyNetwork(player.getServer(), stack, n -> n.getNetworkItemManager().open(player, stack), player::sendMessage);
                }/* TODO else if (stack.getItem() == Item.getItemFromBlock(RSBlocks.PORTABLE_GRID)) { // @Hack
                    API.instance().getGridManager().openGrid(PortableGrid.ID, player, stack);
                }*/
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
