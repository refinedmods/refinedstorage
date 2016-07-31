package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerRelay;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);

        rebuildOnUpdateChange = true;
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.relayUsage;
    }

    @Override
    public void updateNode() {
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
