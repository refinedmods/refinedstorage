package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class PortableGridItemDeltaMessage {
    @Nullable
    private IPortableGrid portableGrid;
    private List<StackListResult<ItemStack>> deltas;

    private List<Pair<IGridStack, Integer>> clientDeltas;

    public PortableGridItemDeltaMessage(IPortableGrid portableGrid, List<StackListResult<ItemStack>> deltas) {
        this.portableGrid = portableGrid;
        this.deltas = deltas;
    }

    public PortableGridItemDeltaMessage(List<Pair<IGridStack, Integer>> clientDeltas) {
        this.clientDeltas = clientDeltas;
    }

    public static PortableGridItemDeltaMessage decode(PacketBuffer buf) {
        int size = buf.readInt();

        List<Pair<IGridStack, Integer>> clientDeltas = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            int delta = buf.readInt();

            clientDeltas.add(Pair.of(StackUtils.readItemGridStack(buf), delta));
        }

        return new PortableGridItemDeltaMessage(clientDeltas);
    }

    public static void encode(PortableGridItemDeltaMessage message, PacketBuffer buf) {
        buf.writeInt(message.deltas.size());

        for (StackListResult<ItemStack> delta : message.deltas) {
            buf.writeInt(delta.getChange());

            StackUtils.writeItemGridStack(buf, delta.getStack(), delta.getId(), null, false, message.portableGrid.getItemStorageTracker().get(delta.getStack()));
        }
    }

    public static void handle(PortableGridItemDeltaMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.clientDeltas.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));

            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
