package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.tile.IReaderWriter;

/**
 * Represents a reader block in the world.
 */
public interface IReader extends IReaderWriter {
    /**
     * @return the redstone strength this reader is receiving
     */
    int getRedstoneStrength();
}
