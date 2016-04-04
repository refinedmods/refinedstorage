package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.container.ContainerRelay;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileMachine {
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

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerRelay.class;
    }
}
