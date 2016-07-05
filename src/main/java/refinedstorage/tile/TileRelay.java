package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerRelay;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    private boolean couldUpdate;

    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.relayUsage;
    }

    @Override
    public void updateNode() {
    }

    public void update() {
        super.update();

        if (network != null && couldUpdate != canUpdate()) {
            couldUpdate = canUpdate();

            network.rebuildNodes();
        }
    }

    @Override
    public boolean canConduct() {
        return canUpdate();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerRelay.class;
    }
}
