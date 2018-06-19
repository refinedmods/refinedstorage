package com.raoulvdberge.refinedstorage.api.network.readerwriter;

/**
 * A listener for reader writers.
 */
public interface IReaderWriterListener {
    /**
     * Called when the listener is attached.
     */
    void onAttached();

    /**
     * Called when a reader writer channel has changed.
     */
    void onChanged();
}
