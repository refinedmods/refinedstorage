package com.refinedmods.refinedstorage.network.tiledata;

import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TileDataParameterMessage {
    private final BlockEntity tile;
    private final TileDataParameter parameter;
    private final boolean initial;

    public TileDataParameterMessage(BlockEntity tile, TileDataParameter parameter, boolean initial) {
        this.tile = tile;
        this.parameter = parameter;
        this.initial = initial;
    }

    public static TileDataParameterMessage decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        boolean initial = buf.readBoolean();

        TileDataParameter parameter = TileDataManager.getParameter(id);

        if (parameter != null) {
            try {
                parameter.setValue(initial, parameter.getSerializer().read(buf));
            } catch (Exception e) {
                // NO OP
            }
        }

        return new TileDataParameterMessage(null, null, initial);
    }

    public static void encode(TileDataParameterMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.parameter.getId());
        buf.writeBoolean(message.initial);

        message.parameter.getSerializer().write(buf, message.parameter.getValueProducer().apply(message.tile));
    }

    public static void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
