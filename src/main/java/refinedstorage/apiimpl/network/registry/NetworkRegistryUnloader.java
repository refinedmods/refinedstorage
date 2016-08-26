package refinedstorage.apiimpl.network.registry;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import refinedstorage.api.RefinedStorageAPI;

public class NetworkRegistryUnloader {
    @Mod.EventHandler
    public void onWorldUnload(WorldEvent.Unload e) {
        RefinedStorageAPI.removeNetworkRegistry(e.getWorld());
    }
}
