package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

/**
 * Represents a reader writer handler.
 * For example: items, fluids, energy, ...
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
     * @param reader     the reader
     * @param capability the capability
     * @return true if we have the given capability for the reader, false otherwise
     */
    boolean hasCapabilityReader(IReader reader, Capability<?> capability);

    /**
     * @param reader     the reader
     * @param capability the capability
     * @return the capability for the given reader
     */
    <T> T getCapabilityReader(IReader reader, Capability<T> capability);

    /**
     * @param writer     the writer
     * @param capability the capability
     * @return true if we have the given capability for the writer, false otherwise
     */
    boolean hasCapabilityWriter(IWriter writer, Capability<?> capability);

    /**
     * @param writer     the writer
     * @param capability the capability
     * @return the capability for the given writer
     */
    <T> T getCapabilityWriter(IWriter writer, Capability<T> capability);

    /**
     * @return a dummy capability that does nothing, for use client side
     */
    Object getNullCapability();

    /**
     * Writes this reader writer handler to NBT.
     *
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeToNbt(NBTTagCompound tag);

    /**
     * @return the id of this writer, as assigned to the {@link IReaderWriterHandlerRegistry}
     */
    String getId();

    /**
     * @param reader  the reader
     * @param channel the channel
     * @return status line(s) displayed when right clicking a reader
     */
    List<ITextComponent> getStatusReader(IReader reader, IReaderWriterChannel channel);

    /**
     * @param writer  the writer
     * @param channel the channel
     * @return status line(s) displayed when right clicking a writer
     */
    List<ITextComponent> getStatusWriter(IWriter writer, IReaderWriterChannel channel);
}
