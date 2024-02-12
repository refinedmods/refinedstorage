package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;

public class NetworkListener {
    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent e) {
        if (!e.level.isClientSide() && e.phase == TickEvent.Phase.END) {
            e.level.getProfiler().push("network ticking");

            for (INetwork network : API.instance().getNetworkManager((ServerLevel) e.level).all()) {
                network.update();
            }

            e.level.getProfiler().pop();

            e.level.getProfiler().push("network node ticking");

            for (INetworkNode node : API.instance().getNetworkNodeManager((ServerLevel) e.level).all()) {
                node.update();
            }

            e.level.getProfiler().pop();
        }
    }
}
