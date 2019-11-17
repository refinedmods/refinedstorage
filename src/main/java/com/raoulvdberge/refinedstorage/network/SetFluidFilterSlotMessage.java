package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFluidFilterSlotMessage {
    private int containerSlot;
    private FluidStack stack;

    public SetFluidFilterSlotMessage(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static SetFluidFilterSlotMessage decode(PacketBuffer buf) {
        return new SetFluidFilterSlotMessage(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void encode(SetFluidFilterSlotMessage message, PacketBuffer buf) {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static void handle(SetFluidFilterSlotMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container != null) {
                    if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                        Slot slot = container.getSlot(message.containerSlot);

                        if (slot instanceof FluidFilterSlot) {
                            FluidFilterSlot fluidFilterSlot = (FluidFilterSlot) slot;

                            // Avoid resetting allowed tag list in the pattern grid.
                            if (API.instance().getComparer().isEqual(fluidFilterSlot.getFluidInventory().getFluid(slot.getSlotIndex()), message.stack, IComparer.COMPARE_NBT)) {
                                fluidFilterSlot.getFluidInventory().getFluid(slot.getSlotIndex()).setAmount(message.stack.getAmount());
                            } else {
                                fluidFilterSlot.getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
                            }
                        }
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
