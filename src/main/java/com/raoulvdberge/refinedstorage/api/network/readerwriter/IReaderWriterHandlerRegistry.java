package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A registry for reader writer handler factories.
 */
public interface IReaderWriterHandlerRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id of this reader writer handler, as specified in {@link IReaderWriterHandler#getId()}
     * @param factory the factory
     */
    void add(String id, IReaderWriterHandlerFactory factory);

    /**
     * Gets a factory from the registry.
     *
     * @param id the id of the factory to get
     * @return the factory, or null if no factory was found
     */
    @Nullable
    IReaderWriterHandlerFactory get(String id);

    /**
     * @return a list of reader writer handler factories
     */
    Collection<IReaderWriterHandlerFactory> all();
}
