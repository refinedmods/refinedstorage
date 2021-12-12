package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class GridItemDeltaMessage {
    @Nullable
    private INetwork network;
    private List<StackListResult<ItemStack>> deltas;

    private List<Pair<IGridStack, Integer>> clientDeltas;

    public GridItemDeltaMessage(INetwork network, List<StackListResult<ItemStack>> deltas) {
        this.network = network;
        this.deltas = deltas;
    }

    public GridItemDeltaMessage(List<Pair<IGridStack, Integer>> clientDeltas) {
        this.clientDeltas = clientDeltas;
    }

    public static GridItemDeltaMessage decode(FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<Pair<IGridStack, Integer>> clientDeltas = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            int delta = buf.readInt();

            clientDeltas.add(Pair.of(StackUtils.readItemGridStack(buf), delta));
        }

        return new GridItemDeltaMessage(clientDeltas);
    }

    public static void encode(GridItemDeltaMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.deltas.size());

        for (StackListResult<ItemStack> delta : message.deltas) {
            buf.writeInt(delta.getChange());

            StackListEntry<ItemStack> craftingEntry = message.network.getItemStorageCache().getCraftablesList().getEntry(delta.getStack(), IComparer.COMPARE_NBT);

            StackUtils.writeItemGridStack(buf, delta.getStack(), delta.getId(), craftingEntry != null ? craftingEntry.getId() : null, false, message.network.getItemStorageTracker().get(delta.getStack()));
        }
    }

    public static void handle(GridItemDeltaMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.clientDeltas.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));
        });

        ctx.get().setPacketHandled(true);
    }
}
