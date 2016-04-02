package refinedstorage.tile.config;

import net.minecraft.util.math.BlockPos;

public interface IRedstoneModeConfig {
    RedstoneMode getRedstoneMode();

    void setRedstoneMode(RedstoneMode mode);

    BlockPos getMachinePos();
}
