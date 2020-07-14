package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Supplier;

public class SetFilterSlotMessage {
    private final int containerSlot;
    private final ItemStack stack;

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
                                // Avoid resetting allowed tag list in the pattern grid.
                                if (API.instance().getComparer().isEqualNoQuantity(slot.getStack(), message.stack)) {
                                    slot.getStack().setCount(message.stack.getCount());

                                    if (slot instanceof FilterSlot) {
                                        IItemHandler itemHandler = ((FilterSlot) slot).getItemHandler();

                                        if (itemHandler instanceof BaseItemHandler) {
                                            ((BaseItemHandler) itemHandler).onChanged(slot.getSlotIndex());
                                        }
                                    } else {
                                        slot.inventory.markDirty();
                                    }
                                } else {
                                    slot.putStack(message.stack);
                                }
                            }
                        }
                    }
                });
            }
        }

        ctx.get().setPacketHandled(true);
    }
}
