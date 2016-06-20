package refinedstorage.api.storagenet;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class StorageNetworkEventHandler {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        Map<BlockPos, StorageNetwork> networks = StorageNetworkRegistry.get(e.world.provider.getDimension());

        if (networks != null) {
            for (StorageNetwork network : networks.values()) {
                if (network.getWorld() == null) {
                    network.onAdded(e.world);
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
