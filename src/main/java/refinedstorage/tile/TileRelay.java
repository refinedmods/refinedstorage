package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.container.ContainerRelay;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    private boolean couldUpdate;

    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.relayRfUsage;
    }

    @Override
    public void updateNode() {
    }

    public void update() {
        super.update();

        if (connected && couldUpdate != canUpdate()) {
            couldUpdate = canUpdate();

            worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.RELAY);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerRelay.class;
    }
}
