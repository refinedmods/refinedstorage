package com.raoulvdberge.refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;

public interface IWrenchable {
    NBTTagCompound writeConfiguration(NBTTagCompound tag);

    void readConfiguration(NBTTagCompound tag);
}
