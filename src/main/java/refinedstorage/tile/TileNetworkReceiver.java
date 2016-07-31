package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerNetworkReceiver;

public class TileNetworkReceiver extends TileNode {
    public TileNetworkReceiver() {
        rebuildOnUpdateChange = true;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.networkReceiverUsage;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerNetworkReceiver.class;
    }
}
