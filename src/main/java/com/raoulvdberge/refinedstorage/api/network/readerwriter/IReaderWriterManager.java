package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

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
     * Sends a channel update to all players watching a reader or writer.
     */
    void sendUpdate();

    /**
     * Sends a channel update to a specific player.
     *
     * @param player the player to send to
     */
    void sendUpdateTo(EntityPlayerMP player);

    /**
     * @param tag the tag to write to
     */
    void writeToNBT(NBTTagCompound tag);

    /**
     * @param tag the tag to read from
     */
    void readFromNBT(NBTTagCompound tag);
}
