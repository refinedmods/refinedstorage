package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.LinkedList;
import java.util.List;

public class GridFluidDeltaMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_fluid_delta");

    private final List<GridStackDelta<FluidGridStack>> deltas;

    public GridFluidDeltaMessage(List<GridStackDelta<FluidGridStack>> deltas) {
        this.deltas = deltas;
    }

    public static GridFluidDeltaMessage decode(FriendlyByteBuf buf) {
        final int size = buf.readInt();
        final List<GridStackDelta<FluidGridStack>> deltas = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            deltas.add(StackUtils.readFluidGridStackDelta(buf));
        }
        return new GridFluidDeltaMessage(deltas);
    }

    public static void handle(GridFluidDeltaMessage message, PlayPayloadContext ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.deltas.forEach(p -> grid.getView().postChange(p.stack(), p.change()));
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(deltas.size());
        deltas.forEach(delta -> StackUtils.writeFluidGridStackDelta(buf, delta));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
