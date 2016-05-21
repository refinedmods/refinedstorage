package refinedstorage.tile.config;

import net.minecraft.util.math.BlockPos;

public interface IModeConfig {
    void setMode(int mode);

    int getMode();

    BlockPos getMachinePos();
}
