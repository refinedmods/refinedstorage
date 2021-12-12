package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class WirelessCraftingMonitorSettingsUpdateMessage {
    private final Optional<UUID> tabSelected;
    private final int tabPage;

    public WirelessCraftingMonitorSettingsUpdateMessage(Optional<UUID> tabSelected, int tabPage) {
        this.tabSelected = tabSelected;
        this.tabPage = tabPage;
    }

    public static WirelessCraftingMonitorSettingsUpdateMessage decode(FriendlyByteBuf buf) {
        Optional<UUID> tabSelected = Optional.empty();

        if (buf.readBoolean()) {
            tabSelected = Optional.of(buf.readUUID());
        }

        int tabPage = buf.readInt();

        return new WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage);
    }

    public static void encode(WirelessCraftingMonitorSettingsUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.tabSelected.isPresent());

        message.tabSelected.ifPresent(buf::writeUUID);

        buf.writeInt(message.tabPage);
    }

    public static void handle(WirelessCraftingMonitorSettingsUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (player.containerMenu instanceof CraftingMonitorContainer) {
                    ((WirelessCraftingMonitor) ((CraftingMonitorContainer) player.containerMenu).getCraftingMonitor()).setSettings(message.tabSelected, message.tabPage);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
