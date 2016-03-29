package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface IRedstoneModeSetting {
    RedstoneMode getRedstoneMode();

    void setRedstoneMode(RedstoneMode mode);

    BlockPos getMachinePos();
}
