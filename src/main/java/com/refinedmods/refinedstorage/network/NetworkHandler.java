package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorCancelMessage;
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage;
import com.refinedmods.refinedstorage.network.craftingmonitor.WirelessCraftingMonitorSettingsUpdateMessage;
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeRequestMessage;
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeResponseMessage;
import com.refinedmods.refinedstorage.network.grid.GridClearMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartRequestMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartResponseMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidInsertHeldMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidPullMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemGridScrollMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemInsertHeldMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemInventoryScrollMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemPullMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridPatternCreateMessage;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.network.grid.PortableGridSettingsUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.WirelessFluidGridSettingsUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.WirelessGridSettingsUpdateMessage;
import com.refinedmods.refinedstorage.network.sync.BlockEntitySynchronizationParameterMessage;
import com.refinedmods.refinedstorage.network.sync.BlockEntitySynchronizationParameterUpdateMessage;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class NetworkHandler {
    public void register(IPayloadRegistrar registrar) {
        registrar.play(
            StorageDiskSizeRequestMessage.ID,
            StorageDiskSizeRequestMessage::decode,
            handler -> handler.server(StorageDiskSizeRequestMessage::handle)
        );
        registrar.play(
            StorageDiskSizeResponseMessage.ID,
            StorageDiskSizeResponseMessage::decode,
            handler -> handler.client(StorageDiskSizeResponseMessage::handle)
        );
        registrar.play(
            FilterUpdateMessage.ID,
            FilterUpdateMessage::decode,
            handler -> handler.server(FilterUpdateMessage::handle)
        );
        registrar.play(
            FluidFilterSlotUpdateMessage.ID,
            FluidFilterSlotUpdateMessage::decode,
            handler -> handler.client(FluidFilterSlotUpdateMessage::handle)
        );
        registrar.play(
            BlockEntitySynchronizationParameterMessage.ID,
            BlockEntitySynchronizationParameterMessage::decode,
            handler -> handler.client(BlockEntitySynchronizationParameterMessage::handle)
        );
        registrar.play(
            BlockEntitySynchronizationParameterUpdateMessage.ID,
            BlockEntitySynchronizationParameterUpdateMessage::decode,
            handler -> handler.server(BlockEntitySynchronizationParameterUpdateMessage::handle)
        );
        registrar.play(
            GridItemUpdateMessage.ID,
            GridItemUpdateMessage::decode,
            handler -> handler.client(GridItemUpdateMessage::handle)
        );
        registrar.play(
            GridItemDeltaMessage.ID,
            GridItemDeltaMessage::decode,
            handler -> handler.client(GridItemDeltaMessage::handle)
        );
        registrar.play(
            GridItemPullMessage.ID,
            GridItemPullMessage::decode,
            handler -> handler.server(GridItemPullMessage::handle)
        );
        registrar.play(
            GridItemGridScrollMessage.ID,
            GridItemGridScrollMessage::decode,
            handler -> handler.server(GridItemGridScrollMessage::handle)
        );
        registrar.play(
            GridItemInventoryScrollMessage.ID,
            GridItemInventoryScrollMessage::decode,
            handler -> handler.server(GridItemInventoryScrollMessage::handle)
        );
        registrar.play(
            GridItemInsertHeldMessage.ID,
            GridItemInsertHeldMessage::decode,
            handler -> handler.server(GridItemInsertHeldMessage::handle)
        );
        registrar.play(
            GridClearMessage.ID,
            ctx -> new GridClearMessage(),
            handler -> handler.server(GridClearMessage::handle)
        );
        registrar.play(
            GridPatternCreateMessage.ID,
            GridPatternCreateMessage::decode,
            handler -> handler.server(GridPatternCreateMessage::handle)
        );
        registrar.play(
            SetFilterSlotMessage.ID,
            SetFilterSlotMessage::decode,
            handler -> handler.server(SetFilterSlotMessage::handle)
        );
        registrar.play(
            SetFluidFilterSlotMessage.ID,
            SetFluidFilterSlotMessage::decode,
            handler -> handler.server(SetFluidFilterSlotMessage::handle)
        );
        registrar.play(
            GridFluidUpdateMessage.ID,
            GridFluidUpdateMessage::decode,
            handler -> handler.client(GridFluidUpdateMessage::handle)
        );
        registrar.play(
            GridFluidDeltaMessage.ID,
            GridFluidDeltaMessage::decode,
            handler -> handler.client(GridFluidDeltaMessage::handle)
        );
        registrar.play(
            GridFluidInsertHeldMessage.ID,
            ctx -> new GridFluidInsertHeldMessage(),
            handler -> handler.server(GridFluidInsertHeldMessage::handle)
        );
        registrar.play(
            GridFluidPullMessage.ID,
            GridFluidPullMessage::decode,
            handler -> handler.server(GridFluidPullMessage::handle)
        );
        registrar.play(
            GridTransferMessage.ID,
            GridTransferMessage::decode,
            handler -> handler.server(GridTransferMessage::handle)
        );
        registrar.play(
            GridProcessingTransferMessage.ID,
            GridProcessingTransferMessage::decode,
            handler -> handler.server(GridProcessingTransferMessage::handle)
        );
        registrar.play(
            SecurityManagerUpdateMessage.ID,
            SecurityManagerUpdateMessage::decode,
            handler -> handler.server(SecurityManagerUpdateMessage::handle)
        );
        registrar.play(
            WirelessGridSettingsUpdateMessage.ID,
            WirelessGridSettingsUpdateMessage::decode,
            handler -> handler.server(WirelessGridSettingsUpdateMessage::handle)
        );
        registrar.play(
            OpenNetworkItemMessage.ID,
            OpenNetworkItemMessage::decode,
            handler -> handler.server(OpenNetworkItemMessage::handle)
        );
        registrar.play(
            WirelessFluidGridSettingsUpdateMessage.ID,
            WirelessFluidGridSettingsUpdateMessage::decode,
            handler -> handler.server(WirelessFluidGridSettingsUpdateMessage::handle)
        );
        registrar.play(
            PortableGridSettingsUpdateMessage.ID,
            PortableGridSettingsUpdateMessage::decode,
            handler -> handler.server(PortableGridSettingsUpdateMessage::handle)
        );
        registrar.play(
            GridCraftingPreviewRequestMessage.ID,
            GridCraftingPreviewRequestMessage::decode,
            handler -> handler.server(GridCraftingPreviewRequestMessage::handle)
        );
        registrar.play(
            GridCraftingPreviewResponseMessage.ID,
            GridCraftingPreviewResponseMessage::decode,
            handler -> handler.client(GridCraftingPreviewResponseMessage::handle)
        );
        registrar.play(
            GridCraftingStartRequestMessage.ID,
            GridCraftingStartRequestMessage::decode,
            handler -> handler.server(GridCraftingStartRequestMessage::handle)
        );
        registrar.play(
            GridCraftingStartResponseMessage.ID,
            buf -> new GridCraftingStartResponseMessage(),
            handler -> handler.client(GridCraftingStartResponseMessage::handle)
        );
        registrar.play(
            CraftingMonitorUpdateMessage.ID,
            CraftingMonitorUpdateMessage::decode,
            handler -> handler.client(CraftingMonitorUpdateMessage::handle)
        );
        registrar.play(
            CraftingMonitorCancelMessage.ID,
            CraftingMonitorCancelMessage::decode,
            handler -> handler.server(CraftingMonitorCancelMessage::handle)
        );
        registrar.play(
            WirelessCraftingMonitorSettingsUpdateMessage.ID,
            WirelessCraftingMonitorSettingsUpdateMessage::decode,
            handler -> handler.server(WirelessCraftingMonitorSettingsUpdateMessage::handle)
        );
    }

    public void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer)) {
            PacketDistributor.PLAYER.with(player).send(message);
        }
    }

    public void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }
}
