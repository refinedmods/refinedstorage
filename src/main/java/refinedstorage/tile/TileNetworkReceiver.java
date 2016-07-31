package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;
import refinedstorage.tile.config.RedstoneMode;

public class TileNetworkReceiver extends TileNode {
    @Override
    public void updateNode() {
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.networkReceiverUsage;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
