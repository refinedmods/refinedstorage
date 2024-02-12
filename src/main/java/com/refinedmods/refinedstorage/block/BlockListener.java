package com.refinedmods.refinedstorage.block;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class BlockListener {
    // Allow break without tool
    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BaseBlock) {
            e.setCanHarvest(true);
        }
    }
}
