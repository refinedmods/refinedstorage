package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.item.WirelessCraftingMonitorItem;
import com.raoulvdberge.refinedstorage.network.craftingmonitor.WirelessCraftingMonitorSettingsUpdateMessage;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private ItemStack stack;
    @Nullable
    private MinecraftServer server;
    private DimensionType nodeDimension;
    private BlockPos nodePos;
    private int tabPage;
    private Optional<UUID> tabSelected;

    public WirelessCraftingMonitor(ItemStack stack, @Nullable MinecraftServer server) {
        this.stack = stack;
        this.server = server;

        this.nodeDimension = WirelessCraftingMonitorItem.getDimension(stack);
        this.nodePos = new BlockPos(WirelessCraftingMonitorItem.getX(stack), WirelessCraftingMonitorItem.getY(stack), WirelessCraftingMonitorItem.getZ(stack));
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
        World world = DimensionManager.getWorld(server, nodeDimension, true, true);

        if (world != null) {
            return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(world.getTileEntity(nodePos)));
        }

        return null;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isActive() {
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
}
