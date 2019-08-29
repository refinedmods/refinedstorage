package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A manager for the reader writer.
 */
public interface IReaderWriterManager {
    /**
     * Updates the channels.
     */
    void update();

    /**
     * @param name the name of the channel
     * @return the channel, or null if nothing was found
     */
    @Nullable
    IReaderWriterChannel getChannel(String name);

    /**
     * Adds a new channel.
     *
     * @param name the name of this channel
     */
    void addChannel(String name);

    /**
     * Removes a channel.
     *
     * @param name the name of the channel to remove
     */
    void removeChannel(String name);

    /**
     * @return a collection of channels
     */
    Collection<String> getChannels();

    /**
     * Adds a listener.
     *
     * @param listener the listener
     */
    void addListener(IReaderWriterListener listener);

    /**
     * Removes a listener.
     *
     * @param listener the listener
     */
    void removeListener(IReaderWriterListener listener);

    /**
     * @param tag the tag to write to
     */
    void writeToNbt(CompoundNBT tag);

    /**
     * @param tag the tag to read from
     */
    void readFromNbt(CompoundNBT tag);
}
