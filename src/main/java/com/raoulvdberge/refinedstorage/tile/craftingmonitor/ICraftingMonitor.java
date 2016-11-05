package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public interface ICraftingMonitor {
    void onCancelled(EntityPlayerMP player, int id);

    BlockPos getNetworkPosition();

    boolean isConnected();
}
