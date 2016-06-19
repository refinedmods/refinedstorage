package refinedstorage.api.storagenet;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StorageNetworkEventHandler {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        // @todo: !!!
        if (e.world.provider.getDimension() == 0) {
            for (StorageNetwork network : StorageNetworkRegistry.NETWORKS.values()) {
                if (network.getWorld() == null) {
                    network.setWorld(e.world);
                }

                network.update();
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        StorageNetworkSavedData.get(e.getWorld());
    }
}
