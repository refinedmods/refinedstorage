package refinedstorage.tile.config;

import net.minecraft.util.math.BlockPos;

public interface IModeConfig {
    boolean isWhitelist();

    boolean isBlacklist();

    void setToWhitelist();

    void setToBlacklist();

    BlockPos getMachinePos();
}
