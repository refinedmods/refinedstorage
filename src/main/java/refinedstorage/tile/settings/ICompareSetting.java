package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface ICompareSetting {
    int getCompare();

    void setCompare(int compare);

    BlockPos getMachinePos();
}
