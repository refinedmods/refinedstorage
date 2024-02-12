package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.util.StackUtils;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridItemDeltaMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_delta");

    private final List<GridStackDelta<ItemGridStack>> deltas;

    public GridItemDeltaMessage(List<GridStackDelta<ItemGridStack>> deltas) {
        this.deltas = deltas;
    }

    public static GridItemDeltaMessage decode(FriendlyByteBuf buf) {
        final int size = buf.readInt();
        final List<GridStackDelta<ItemGridStack>> deltas = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            deltas.add(StackUtils.readItemGridStackDelta(buf));
        }
        return new GridItemDeltaMessage(deltas);
    }

    public static void handle(GridItemDeltaMessage message, PlayPayloadContext ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            message.deltas.forEach(p -> grid.getView().postChange(p.stack(), p.change()));
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(deltas.size());
        deltas.forEach(delta -> StackUtils.writeItemGridStackDelta(buf, delta));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
