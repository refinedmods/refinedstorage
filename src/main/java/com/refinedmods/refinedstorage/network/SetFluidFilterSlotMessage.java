package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SetFluidFilterSlotMessage {
    private final int containerSlot;
    private final FluidStack stack;

    public SetFluidFilterSlotMessage(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static SetFluidFilterSlotMessage decode(FriendlyByteBuf buf) {
        return new SetFluidFilterSlotMessage(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void encode(SetFluidFilterSlotMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static void handle(SetFluidFilterSlotMessage message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.stack.isEmpty()) {
            Player player = ctx.get().getSender();

            if (player != null) {
                ctx.get().enqueueWork(() -> {
                    AbstractContainerMenu container = player.containerMenu;

                    if (container != null && message.containerSlot >= 0 && message.containerSlot < container.slots.size()) {
                        handle(message, container);
                    }
                });
            }
        }

        ctx.get().setPacketHandled(true);
    }

    private static void handle(SetFluidFilterSlotMessage message, AbstractContainerMenu container) {
        Slot slot = container.getSlot(message.containerSlot);

        if (slot instanceof FluidFilterSlot) {
            Runnable postAction = () -> {
            };

            // Prevent the grid crafting matrix inventory listener from resetting the list.
            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();
                if (grid instanceof GridNetworkNode && slot.getSlotIndex() < ((GridNetworkNode) grid).getAllowedTagList().getAllowedFluidTags().size()) {
                    Set<ResourceLocation> list = new HashSet<>(((GridNetworkNode) grid).getAllowedTagList().getAllowedFluidTags().get(slot.getSlotIndex()));

                    postAction = () -> {
                        ((GridNetworkNode) grid).getAllowedTagList().setAllowedFluidTags(slot.getSlotIndex(), list);
                        ((GridNetworkNode) grid).markDirty();
                    };
                }
            }

            FluidFilterSlot fluidSlot = (FluidFilterSlot) slot;

            fluidSlot.getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
            postAction.run();
        }
    }
}
