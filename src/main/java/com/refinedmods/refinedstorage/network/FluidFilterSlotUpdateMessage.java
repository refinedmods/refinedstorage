package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class FluidFilterSlotUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid_filter_update");

    private final int containerSlot;
    private final FluidStack stack;

    public FluidFilterSlotUpdateMessage(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static FluidFilterSlotUpdateMessage decode(FriendlyByteBuf buf) {
        return new FluidFilterSlotUpdateMessage(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void handle(FluidFilterSlotUpdateMessage message, PlayPayloadContext ctx) {
        BaseScreen.executeLater(gui -> {
            if (message.containerSlot >= 0 && message.containerSlot < gui.getMenu().slots.size()) {
                Slot slot = gui.getMenu().getSlot(message.containerSlot);

                if (slot instanceof FluidFilterSlot) {
                    ((FluidFilterSlot) slot).getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
                }
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(containerSlot);
        stack.writeToPacket(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
