package com.raoulvdberge.refinedstorage.block;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockListener {
    // Allow break without tool
    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BaseBlock) {
            e.setCanHarvest(true);
        }
    }
}
