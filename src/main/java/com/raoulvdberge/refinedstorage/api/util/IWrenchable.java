package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Implement this on a tile that is wrenchable.
 */
public interface IWrenchable {
    /**
     * Writes the configuration of this tile to the wrench.
     *
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeConfiguration(NBTTagCompound tag);

    /**
     * Reads the configuration of this tile from the wrench.
     *
     * @param tag the tag to read
     */
    void readConfiguration(NBTTagCompound tag);
}
