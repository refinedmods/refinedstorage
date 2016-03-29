package refinedstorage.tile;

import refinedstorage.tile.settings.RedstoneMode;

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
}
