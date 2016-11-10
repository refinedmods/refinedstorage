package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IReaderWriterChannel {
    void addHandlers();

    List<IReaderWriterHandler> getHandlers();

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
