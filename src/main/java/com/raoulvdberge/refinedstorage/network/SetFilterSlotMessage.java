package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFilterSlotMessage {
    private int containerSlot;
    private ItemStack stack;

    public SetFilterSlotMessage(int containerSlot, ItemStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static SetFilterSlotMessage decode(PacketBuffer buf) {
        return new SetFilterSlotMessage(buf.readInt(), buf.readItemStack());
    }

    public static void encode(SetFilterSlotMessage message, PacketBuffer buf) {
        buf.writeInt(message.containerSlot);
        buf.writeItemStack(message.stack);
    }

    public static void handle(SetFilterSlotMessage message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.stack.isEmpty() && message.stack.getCount() <= message.stack.getMaxStackSize()) {
            PlayerEntity player = ctx.get().getSender();

            if (player != null) {
                ctx.get().enqueueWork(() -> {
                    Container container = player.openContainer;

                    if (container != null) {
                        if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                            Slot slot = container.getSlot(message.containerSlot);

                            if (slot instanceof FilterSlot || slot instanceof LegacyFilterSlot) {
                                slot.putStack(message.stack);
                            }
                        }
                    }
                });
            }
        }

        ctx.get().setPacketHandled(true);
    }
}
