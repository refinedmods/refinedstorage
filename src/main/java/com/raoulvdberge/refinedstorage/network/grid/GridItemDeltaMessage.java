package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class GridItemDeltaMessage {
    @Nullable
    private INetwork network;
    private List<Pair<ItemStack, Integer>> deltas;

    private List<Pair<IGridStack, Integer>> gridStacks;

    public GridItemDeltaMessage(INetwork network, List<Pair<ItemStack, Integer>> deltas) {
        this.network = network;
        this.deltas = deltas;
    }

    public GridItemDeltaMessage(List<Pair<IGridStack, Integer>> gridStacks) {
        this.gridStacks = gridStacks;
    }

    public static GridItemDeltaMessage decode(PacketBuffer buf) {
        int size = buf.readInt();

        List<Pair<IGridStack, Integer>> gridStacks = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            gridStacks.add(Pair.of(StackUtils.readItemGridStack(buf), buf.readInt()));
        }

        return new GridItemDeltaMessage(gridStacks);
    }

    public static void encode(GridItemDeltaMessage message, PacketBuffer buf) {
        buf.writeInt(message.deltas.size());

        for (Pair<ItemStack, Integer> delta : message.deltas) {
            StackUtils.writeItemGridStack(buf, delta.getLeft(), message.network, false, message.network.getItemStorageTracker().get(delta.getLeft()));

            buf.writeInt(delta.getRight());
        }
    }

    public static void handle(GridItemDeltaMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.gridStacks.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));

            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
