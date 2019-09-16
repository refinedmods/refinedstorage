package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RS;
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

        handler.registerMessage(id++, MessageStorageDiskSizeRequest.class, MessageStorageDiskSizeRequest::encode, MessageStorageDiskSizeRequest::decode, MessageStorageDiskSizeRequest::handle);
        handler.registerMessage(id++, MessageStorageDiskSizeResponse.class, MessageStorageDiskSizeResponse::encode, MessageStorageDiskSizeResponse::decode, MessageStorageDiskSizeResponse::handle);
        handler.registerMessage(id++, MessageFilterUpdate.class, MessageFilterUpdate::encode, MessageFilterUpdate::decode, MessageFilterUpdate::handle);
        handler.registerMessage(id++, MessageSlotFilterFluidUpdate.class, MessageSlotFilterFluidUpdate::encode, MessageSlotFilterFluidUpdate::decode, MessageSlotFilterFluidUpdate::handle);
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
