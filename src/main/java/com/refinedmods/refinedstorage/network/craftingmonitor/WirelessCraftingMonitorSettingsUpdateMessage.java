package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.WirelessCraftingMonitor;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class WirelessCraftingMonitorSettingsUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "wireless_crafting_monitor_settings_update");

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

    public static void handle(WirelessCraftingMonitorSettingsUpdateMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof CraftingMonitorContainerMenu) {
                ((WirelessCraftingMonitor) ((CraftingMonitorContainerMenu) player.containerMenu).getCraftingMonitor()).setSettings(
                    message.tabSelected, message.tabPage);
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(tabSelected.isPresent());

        tabSelected.ifPresent(buf::writeUUID);

        buf.writeInt(tabPage);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
