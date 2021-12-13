package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class PortableGridFluidDeltaMessage {
    private IPortableGrid portableGrid;
    private List<StackListResult<FluidStack>> deltas;

    private List<Pair<IGridStack, Integer>> clientDeltas;

    public PortableGridFluidDeltaMessage(IPortableGrid portableGrid, List<StackListResult<FluidStack>> deltas) {
        this.portableGrid = portableGrid;
        this.deltas = deltas;
    }

    public PortableGridFluidDeltaMessage(List<Pair<IGridStack, Integer>> clientDeltas) {
        this.clientDeltas = clientDeltas;
    }

    public static PortableGridFluidDeltaMessage decode(FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<Pair<IGridStack, Integer>> clientDeltas = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            int delta = buf.readInt();

            clientDeltas.add(Pair.of(StackUtils.readFluidGridStack(buf), delta));
        }

        return new PortableGridFluidDeltaMessage(clientDeltas);
    }

    public static void encode(PortableGridFluidDeltaMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.deltas.size());

        for (StackListResult<FluidStack> delta : message.deltas) {
            buf.writeInt(delta.getChange());

            StackUtils.writeFluidGridStack(buf, delta.getStack(), delta.getId(), null, false, message.portableGrid.getFluidStorageTracker().get(delta.getStack()));
        }
    }

    public static void handle(PortableGridFluidDeltaMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.clientDeltas.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));
        });

        ctx.get().setPacketHandled(true);
    }
}
