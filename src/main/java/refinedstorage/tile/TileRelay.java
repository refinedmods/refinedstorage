package refinedstorage.tile;

import net.minecraft.util.EnumFacing;
import refinedstorage.RS;
import refinedstorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);

        rebuildOnUpdateChange = true;
    }

    @Override
    public int getEnergyUsage() {
        return getRedstoneMode() == RedstoneMode.IGNORE ? 0 : RS.INSTANCE.config.relayUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return canUpdate();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
