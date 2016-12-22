package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface INetworkNodeRegistry {
    void add(String id, Function<NBTTagCompound, INetworkNode> factory);

    @Nullable
    Function<NBTTagCompound, INetworkNode> get(String id);
}
