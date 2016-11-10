package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IReaderWriterHandlerFactory {
    @Nonnull
    IReaderWriterHandler create(@Nullable NBTTagCompound tag);
}
