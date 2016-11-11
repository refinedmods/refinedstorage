package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Represents a reader writer handler. Can be for example: items, fluids, energy, ...
 */
public interface IReaderWriterHandler extends ICapabilityProvider {
    /**
     * Updates this reader writer handler.
     *
     * @param channel the channel this reader writer handler is assigned to
     */
    void update(IReaderWriterChannel channel);

    /**
     * Called when the network connection state changes.
     *
     * @param state the new connection state
     */
    void onConnectionChange(boolean state);

    /**
     * Writes this reader writer handler to NBT.
     *
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * @return the id of this writer, as assigned to the {@link IReaderWriterHandlerRegistry}
     */
    String getId();
}
