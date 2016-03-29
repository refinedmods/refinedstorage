package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface IModeSetting {
    boolean isWhitelist();

    boolean isBlacklist();

    void setToWhitelist();

    void setToBlacklist();

    BlockPos getMachinePos();
}
