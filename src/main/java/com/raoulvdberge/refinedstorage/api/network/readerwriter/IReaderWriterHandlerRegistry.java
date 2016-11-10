package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IReaderWriterHandlerRegistry {
    void add(String id, IReaderWriterHandlerFactory factory);

    @Nullable
    IReaderWriterHandlerFactory getFactory(String id);

    Collection<IReaderWriterHandlerFactory> getFactories();
}
