package com.refinedmods.refinedstorage.tile.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem;
import com.refinedmods.refinedstorage.network.craftingmonitor.WirelessCraftingMonitorSettingsUpdateMessage;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private final ItemStack stack;
    @Nullable
    private final MinecraftServer server;
    private final RegistryKey<World> nodeDimension;
    private final BlockPos nodePos;
    private int tabPage;
    private Optional<UUID> tabSelected;
    private final PlayerSlot slot;

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
    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui.refinedstorage.wireless_crafting_monitor");
    }

    @Override
    public void onCancelled(ServerPlayerEntity player, @Nullable UUID id) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
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
        World world = server.getLevel(nodeDimension);
        if (world != null) {
            return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(world.getBlockEntity(nodePos)));
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
    public void onClosed(PlayerEntity player) {
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
