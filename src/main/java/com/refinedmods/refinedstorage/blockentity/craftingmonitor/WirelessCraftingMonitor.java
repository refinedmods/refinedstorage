package com.refinedmods.refinedstorage.blockentity.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem;
import com.refinedmods.refinedstorage.network.craftingmonitor.WirelessCraftingMonitorSettingsUpdateMessage;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private final ItemStack stack;
    @Nullable
    private final MinecraftServer server;
    private final ResourceKey<Level> nodeDimension;
    private final BlockPos nodePos;
    private final PlayerSlot slot;
    private int tabPage;
    private Optional<UUID> tabSelected;

    public WirelessCraftingMonitor(ItemStack stack, @Nullable MinecraftServer server, PlayerSlot slot) {
        this.stack = stack;
        this.server = server;
        this.slot = slot;

        this.nodeDimension = NetworkItem.getDimension(stack);
        this.nodePos = new BlockPos(NetworkItem.getX(stack), NetworkItem.getY(stack), NetworkItem.getZ(stack));
        this.tabPage = WirelessCraftingMonitorItem.getTabPage(stack);
        this.tabSelected = WirelessCraftingMonitorItem.getTabSelected(stack);
    }

    public void setSettings(Optional<UUID> tabSelected, int tabPage) {
        this.tabSelected = tabSelected;
        this.tabPage = tabPage;

        WirelessCraftingMonitorItem.setTabSelected(stack, tabSelected);
        WirelessCraftingMonitorItem.setTabPage(stack, tabPage);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("gui.refinedstorage.wireless_crafting_monitor");
    }

    @Override
    public void onCancelled(ServerPlayer player, @Nullable UUID id) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public BlockEntitySynchronizationParameter<Integer, ?> getRedstoneModeParameter() {
        return null;
    }

    @Override
    public Collection<ICraftingTask> getTasks() {
        INetwork network = getNetwork();

        if (network != null) {
            return network.getCraftingManager().getTasks();
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public ICraftingManager getCraftingManager() {
        INetwork network = getNetwork();

        if (network != null) {
            return network.getCraftingManager();
        }

        return null;
    }

    private INetwork getNetwork() {
        Level level = server.getLevel(nodeDimension);
        if (level != null) {
            return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeAtPosition(level, nodePos));
        }
        return null;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isActiveOnClient() {
        return true;
    }

    @Override
    public void onClosed(Player player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemManager().close(player);
        }
    }

    @Override
    public Optional<UUID> getTabSelected() {
        return tabSelected;
    }

    @Override
    public int getTabPage() {
        return tabPage;
    }

    @Override
    public void onTabSelectionChanged(Optional<UUID> taskId) {
        if (taskId.isPresent() && tabSelected.isPresent() && taskId.get().equals(tabSelected.get())) {
            this.tabSelected = Optional.empty();
        } else {
            this.tabSelected = taskId;
        }

        RS.NETWORK_HANDLER.sendToServer(new WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage));
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0) {
            this.tabPage = page;

            RS.NETWORK_HANDLER.sendToServer(new WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage));
        }
    }

    @Override
    public int getSlotId() {
        return slot.getSlotIdInPlayerInventory();
    }
}
