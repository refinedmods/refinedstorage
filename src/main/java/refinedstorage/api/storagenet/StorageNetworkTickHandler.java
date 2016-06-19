package refinedstorage.api.storagenet;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StorageNetworkTickHandler {
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        for (StorageNetwork network : StorageNetworkRegistry.NETWORKS.values()) {
            network.update();
        }
    }
}
