package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public interface ICraftingMonitor {
    void onCancelled(EntityPlayerMP player, int id);

    TileDataParameter<Integer> getRedstoneModeParameter();

    BlockPos getNetworkPosition();

    boolean isConnected();
}
