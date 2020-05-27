package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkListener {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote()) {
            if (e.phase == TickEvent.Phase.END) {
                e.world.getProfiler().startSection("network ticking");

                for (INetwork network : API.instance().getNetworkManager((ServerWorld) e.world).all()) {
                    network.update();
                }

                e.world.getProfiler().endSection();
            }
        }
    }
}
