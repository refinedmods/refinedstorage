package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class NetworkNodeCraftingMonitor extends NetworkNode implements ICraftingMonitor {
    public NetworkNodeCraftingMonitor(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:crafting_monitor";
    }

    @Override
    public void onCancelled(EntityPlayerMP player, int id) {
        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return TileCraftingMonitor.REDSTONE_MODE;
    }

    @Nullable
    @Override
    public BlockPos getNetworkPosition() {
        return network != null ? network.getPosition() : null;
    }

    public void onOpened(EntityPlayer player) {
        if (network != null) {
            network.sendCraftingMonitorUpdate((EntityPlayerMP) player);
        }
    }
}
