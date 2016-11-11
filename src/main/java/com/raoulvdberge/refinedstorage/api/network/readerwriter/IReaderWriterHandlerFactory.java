package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A factory that is able to create reader writer handlers.
 */
public interface IReaderWriterHandlerFactory {
    /**
     * Creates a reader writer handler (based on NBT tag if there is any).
     *
     * @param tag the tag to read from, null if this reader writer handler is created on demand
     * @return the reader writer handler
     */
    @Nonnull
    IReaderWriterHandler create(@Nullable NBTTagCompound tag);
}
