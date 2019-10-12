package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class GridFluidDeltaMessage {
    @Nullable
    private INetwork network;
    private List<StackListResult<FluidStack>> deltas;

    private List<Pair<IGridStack, Integer>> clientDeltas;

    public GridFluidDeltaMessage(INetwork network, List<StackListResult<FluidStack>> deltas) {
        this.network = network;
        this.deltas = deltas;
    }

    public GridFluidDeltaMessage(List<Pair<IGridStack, Integer>> clientDeltas) {
        this.clientDeltas = clientDeltas;
    }

    public static GridFluidDeltaMessage decode(PacketBuffer buf) {
        int size = buf.readInt();

        List<Pair<IGridStack, Integer>> clientDeltas = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            int delta = buf.readInt();

            clientDeltas.add(Pair.of(StackUtils.readFluidGridStack(buf), delta));
        }

        return new GridFluidDeltaMessage(clientDeltas);
    }

    public static void encode(GridFluidDeltaMessage message, PacketBuffer buf) {
        buf.writeInt(message.deltas.size());

        for (StackListResult<FluidStack> delta : message.deltas) {
            buf.writeInt(delta.getChange());

            StackUtils.writeFluidGridStack(buf, delta.getStack(), delta.getId(), message.network, false, message.network.getFluidStorageTracker().get(delta.getStack()));
        }
    }

    public static void handle(GridFluidDeltaMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.clientDeltas.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));

            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
