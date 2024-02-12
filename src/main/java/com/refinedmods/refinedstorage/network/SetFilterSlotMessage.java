package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetFilterSlotMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "set_filter_slot");

    private final int containerSlot;
    private final ItemStack stack;

    public SetFilterSlotMessage(int containerSlot, ItemStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static SetFilterSlotMessage decode(FriendlyByteBuf buf) {
        return new SetFilterSlotMessage(buf.readInt(), buf.readItem());
    }

    public static void handle(SetFilterSlotMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (!message.stack.isEmpty() && message.stack.getCount() <= message.stack.getMaxStackSize()) {
                AbstractContainerMenu container = player.containerMenu;

                if (container != null && message.containerSlot >= 0 && message.containerSlot < container.slots.size()) {
                    handle(message, container);
                }
            }
        }));
    }

    private static void handle(SetFilterSlotMessage message, AbstractContainerMenu container) {
        Slot slot = container.getSlot(message.containerSlot);

        if (slot instanceof FilterSlot || slot instanceof LegacyFilterSlot) {
            Runnable postAction = () -> {
            };

            // Prevent the grid crafting matrix inventory listener from resetting the list.
            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();
                //exclude output slots
                if (grid instanceof GridNetworkNode &&
                    slot.getSlotIndex() < ((GridNetworkNode) grid).getAllowedTagList().getAllowedItemTags().size()) {
                    Set<ResourceLocation> list = new HashSet<>(
                        ((GridNetworkNode) grid).getAllowedTagList().getAllowedItemTags().get(slot.getSlotIndex()));

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

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(containerSlot);
        buf.writeItem(stack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
