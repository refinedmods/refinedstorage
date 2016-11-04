package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ICraftingMonitor {
    void onCancelled(EntityPlayerMP player, int id);
}
