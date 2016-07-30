package refinedstorage.tile;

import net.minecraft.inventory.Container;

public class TileNetworkReceiver extends TileNode {
    @Override
    public void updateNode() {
    }

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }
}
