package com.raoulvdberge.refinedstorage.tile;

public interface IReaderWriterGui {
    String getTitle();

    void onAdd(String name);

    void onRemove(String name);
}
