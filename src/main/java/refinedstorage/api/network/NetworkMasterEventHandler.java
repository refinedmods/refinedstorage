package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class NetworkMasterEventHandler {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        Map<BlockPos, NetworkMaster> networks = NetworkMasterRegistry.get(e.world.provider.getDimension());

        if (networks != null) {
            for (NetworkMaster network : networks.values()) {
                if (network.getWorld() == null) {
                    network.onAdded(e.world);
                }

                network.update();
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        NetworkMasterSavedData.getOrLoad(e.getWorld());
    }
}
