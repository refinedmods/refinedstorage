package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.network.MessageWirelessCraftingMonitorSettings;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private ItemStack stack;

    private int networkDimension;
    private BlockPos network;
    private int tabPage;
    private Optional<UUID> tabSelected;
    private int slotId;

    public WirelessCraftingMonitor(ItemStack stack, int slotId) {
        this.stack = stack;
        this.slotId = slotId;
        this.networkDimension = ItemWirelessCraftingMonitor.getDimensionId(stack);
        this.network = new BlockPos(ItemWirelessCraftingMonitor.getX(stack), ItemWirelessCraftingMonitor.getY(stack), ItemWirelessCraftingMonitor.getZ(stack));
        this.tabPage = ItemWirelessCraftingMonitor.getTabPage(stack);
        this.tabSelected = ItemWirelessCraftingMonitor.getTabSelected(stack);
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:wireless_crafting_monitor";
    }

    @Override
    public void onCancelled(EntityPlayerMP player, @Nullable UUID id) {
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
        World world = DimensionManager.getWorld(networkDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(network);

            return tile instanceof INetwork ? (INetwork) tile : null;
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
    public void onClosed(EntityPlayer player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemHandler().close(player);
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
            this.tabSelected = Optional.absent();
        } else {
            this.tabSelected = taskId;
        }

        RS.INSTANCE.network.sendToServer(new MessageWirelessCraftingMonitorSettings(tabSelected, tabPage));
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0) {
            this.tabPage = page;

            RS.INSTANCE.network.sendToServer(new MessageWirelessCraftingMonitorSettings(tabSelected, tabPage));
        }
    }

    @Override
    public int getSlotId() {
        return slotId;
    }
}
