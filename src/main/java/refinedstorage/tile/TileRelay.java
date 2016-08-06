package refinedstorage.tile;

import net.minecraft.util.EnumFacing;
import refinedstorage.RefinedStorage;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);

        rebuildOnUpdateChange = true;
    }

    @Override
    public int getEnergyUsage() {
        return getRedstoneMode() == RedstoneMode.IGNORE ? 0 : RefinedStorage.INSTANCE.relayUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return canUpdate();
    }
}
