package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.network.disk.StorageDiskSizeRequestMessage;
import com.raoulvdberge.refinedstorage.network.disk.StorageDiskSizeResponseMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridItemDeltaMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridItemInsertHeldMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridItemPullMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridItemUpdateMessage;
import com.raoulvdberge.refinedstorage.network.tiledata.TileDataParameterMessage;
import com.raoulvdberge.refinedstorage.network.tiledata.TileDataParameterUpdateMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    private final String protocolVersion = Integer.toString(1);
    private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(RS.ID, "main_channel"))
        .clientAcceptedVersions(protocolVersion::equals)
        .serverAcceptedVersions(protocolVersion::equals)
        .networkProtocolVersion(() -> protocolVersion)
        .simpleChannel();

    public void register() {
        int id = 0;

        handler.registerMessage(id++, StorageDiskSizeRequestMessage.class, StorageDiskSizeRequestMessage::encode, StorageDiskSizeRequestMessage::decode, StorageDiskSizeRequestMessage::handle);
        handler.registerMessage(id++, StorageDiskSizeResponseMessage.class, StorageDiskSizeResponseMessage::encode, StorageDiskSizeResponseMessage::decode, StorageDiskSizeResponseMessage::handle);
        handler.registerMessage(id++, FilterUpdateMessage.class, FilterUpdateMessage::encode, FilterUpdateMessage::decode, FilterUpdateMessage::handle);
        handler.registerMessage(id++, FluidFilterSlotUpdateMessage.class, FluidFilterSlotUpdateMessage::encode, FluidFilterSlotUpdateMessage::decode, FluidFilterSlotUpdateMessage::handle);
        handler.registerMessage(id++, TileDataParameterMessage.class, TileDataParameterMessage::encode, TileDataParameterMessage::decode, TileDataParameterMessage::handle);
        handler.registerMessage(id++, TileDataParameterUpdateMessage.class, TileDataParameterUpdateMessage::encode, TileDataParameterUpdateMessage::decode, TileDataParameterUpdateMessage::handle);
        handler.registerMessage(id++, GridItemUpdateMessage.class, GridItemUpdateMessage::encode, GridItemUpdateMessage::decode, GridItemUpdateMessage::handle);
        handler.registerMessage(id++, GridItemDeltaMessage.class, GridItemDeltaMessage::encode, GridItemDeltaMessage::decode, GridItemDeltaMessage::handle);
        handler.registerMessage(id++, GridItemPullMessage.class, GridItemPullMessage::encode, GridItemPullMessage::decode, GridItemPullMessage::handle);
        handler.registerMessage(id++, GridItemInsertHeldMessage.class, GridItemInsertHeldMessage::encode, GridItemInsertHeldMessage::decode, GridItemInsertHeldMessage::handle);
    }

    public void sendToServer(Object message) {
        handler.sendToServer(message);
    }

    public void sendTo(ServerPlayerEntity player, Object message) {
        if (!(player instanceof FakePlayer)) {
            handler.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
