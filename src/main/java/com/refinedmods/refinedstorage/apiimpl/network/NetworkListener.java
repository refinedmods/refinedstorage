package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkListener {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isClientSide() && e.phase == TickEvent.Phase.END) {
            e.world.getProfiler().push("network ticking");

            for (INetwork network : API.instance().getNetworkManager((ServerWorld) e.world).all()) {
                network.update();
            }

            e.world.getProfiler().pop();

            e.world.getProfiler().push("network node ticking");

            for (INetworkNode node : API.instance().getNetworkNodeManager((ServerWorld) e.world).all()) {
                node.update();
            }

            e.world.getProfiler().pop();
        }
    }
}
