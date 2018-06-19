package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Represents a reader writer channel.
 */
public interface IReaderWriterChannel {
    /**
     * @return the handlers that this channel has
     */
    List<IReaderWriterHandler> getHandlers();

    /**
     * @return a list of readers using this channel
     */
    List<IReader> getReaders();

    /**
     * @return a list of writers using this channel
     */
    List<IWriter> getWriters();

    /**
     * Writes this channel to NBT.
     *
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeToNbt(NBTTagCompound tag);

    /**
     * Reads this channel from NBT.
     *
     * @param tag the tag to read from
     */
    void readFromNbt(NBTTagCompound tag);
}
