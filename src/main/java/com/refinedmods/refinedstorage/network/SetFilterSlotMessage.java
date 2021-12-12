package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SetFilterSlotMessage {
    private final int containerSlot;
    private final ItemStack stack;

    public SetFilterSlotMessage(int containerSlot, ItemStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static SetFilterSlotMessage decode(PacketBuffer buf) {
        return new SetFilterSlotMessage(buf.readInt(), buf.readItem());
    }

    public static void encode(SetFilterSlotMessage message, PacketBuffer buf) {
        buf.writeInt(message.containerSlot);
        buf.writeItem(message.stack);
    }

    public static void handle(SetFilterSlotMessage message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.stack.isEmpty() && message.stack.getCount() <= message.stack.getMaxStackSize()) {
            PlayerEntity player = ctx.get().getSender();

            if (player != null) {
                ctx.get().enqueueWork(() -> {
                    Container container = player.containerMenu;

                    if (container != null && message.containerSlot >= 0 && message.containerSlot < container.slots.size()) {
                        handle(message, container);
                    }
                });
            }
        }

        ctx.get().setPacketHandled(true);
    }

    private static void handle(SetFilterSlotMessage message, Container container) {
        Slot slot = container.getSlot(message.containerSlot);

        if (slot instanceof FilterSlot || slot instanceof LegacyFilterSlot) {
            Runnable postAction = () -> {
            };

            // Prevent the grid crafting matrix inventory listener from resetting the list.
            if (container instanceof GridContainer) {
                IGrid grid = ((GridContainer) container).getGrid();
                //exclude output slots
                if (grid instanceof GridNetworkNode && slot.getSlotIndex() < ((GridNetworkNode) grid).getAllowedTagList().getAllowedItemTags().size()) {
                    Set<ResourceLocation> list = new HashSet<>(((GridNetworkNode) grid).getAllowedTagList().getAllowedItemTags().get(slot.getSlotIndex()));

                    postAction = () -> {
                        ((GridNetworkNode) grid).getAllowedTagList().setAllowedItemTags(slot.getSlotIndex(), list);
                        ((GridNetworkNode) grid).markDirty();
                    };
                }
            }

            slot.set(message.stack);
            postAction.run();
        }
    }
}
