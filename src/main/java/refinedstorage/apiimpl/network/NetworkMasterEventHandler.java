package refinedstorage.apiimpl.network;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import refinedstorage.api.network.INetworkMaster;

import java.util.Map;

public class NetworkMasterEventHandler {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        Map<BlockPos, INetworkMaster> networks = NetworkMasterRegistry.get(e.world);

        if (networks != null) {
            for (INetworkMaster network : networks.values()) {
                if (network.getWorld() == null) {
                    network.setWorld(e.world);
                }

                network.update();
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        NetworkMasterSavedData.getOrLoad(e.getWorld());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        NetworkMasterRegistry.clear();
    }
}
