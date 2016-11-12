package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Represents a reader writer handler. Can be for example: items, fluids, energy, ...
 */
public interface IReaderWriterHandler {
    /**
     * Updates this reader writer handler.
     *
     * @param channel the channel this reader writer handler is assigned to
     */
    void update(IReaderWriterChannel channel);

    /**
     * Called when this handler is removed from a writer.
     *
     * @param writer the writer
     */
    void onWriterDisabled(IWriter writer);

    /**
     * @param readerWriter the reader writer
     * @param capability   the capability
     * @return whether we have the given capability for the reader writer
     */
    boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability);

    /**
     * @param readerWriter the reader writer
     * @param capability   the capability
     * @return the capability for the given reader writer
     */
    <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability);

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
