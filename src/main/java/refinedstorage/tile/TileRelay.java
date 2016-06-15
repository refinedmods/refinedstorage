package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.container.ContainerRelay;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileMachine {
    private boolean couldUpdate;

    public TileRelay() {
        this.redstoneMode = RedstoneMode.LOW;
    }

    @Override
    public int getEnergyUsage() {
        return 1;
    }

    @Override
    public void updateMachine() {
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
