package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.nbt.NBTTagCompound;

public interface IWrenchable {
    NBTTagCompound writeConfiguration(NBTTagCompound tag);

    void readConfiguration(NBTTagCompound tag);
}
