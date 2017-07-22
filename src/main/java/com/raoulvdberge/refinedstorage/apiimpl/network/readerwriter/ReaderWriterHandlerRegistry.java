package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerFactory;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReaderWriterHandlerRegistry implements IReaderWriterHandlerRegistry {
    private Map<String, IReaderWriterHandlerFactory> factories = new HashMap<>();

    @Override
    public void add(String id, IReaderWriterHandlerFactory factory) {
        factories.put(id, factory);
    }

    @Nullable
    @Override
    public IReaderWriterHandlerFactory get(String id) {
        return factories.get(id);
    }

    @Override
    public Collection<IReaderWriterHandlerFactory> all() {
        return factories.values();
    }
}
