package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import net.minecraft.util.EnumFacing;

public interface IWriter extends INetworkNode {
    int getRedstoneStrength();

    void setRedstoneStrength(int strength);

    EnumFacing getDirection();

    boolean hasStackUpgrade();
}
