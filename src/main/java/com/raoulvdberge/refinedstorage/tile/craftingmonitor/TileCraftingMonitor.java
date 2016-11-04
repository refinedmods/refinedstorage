package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class TileCraftingMonitor extends TileNode implements ICraftingMonitor {
    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void onCancelled(int id) {
        if (isConnected()) {
            network.getItemGridHandler().onCraftingCancelRequested(id);
        }
    }

    public void onOpened(EntityPlayer player) {
        if (isConnected()) {
            network.sendCraftingMonitorUpdate((EntityPlayerMP) player);
        }
    }
}
