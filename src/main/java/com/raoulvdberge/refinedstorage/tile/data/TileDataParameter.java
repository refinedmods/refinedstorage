package com.raoulvdberge.refinedstorage.tile.data;

import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.tileentity.TileEntity;

public class TileDataParameter<T> {
    private int id;
    private DataSerializer<T> serializer;
    private ITileDataProducer<T, ? extends TileEntity> valueProducer;
    private ITileDataConsumer<T, ? extends TileEntity> valueConsumer;
    private ITileDataListener<T> listener;
    private T value;

    public TileDataParameter(DataSerializer<T> serializer, T defaultValue, ITileDataProducer<T, ? extends TileEntity> producer) {
        this(serializer, defaultValue, producer, null);
    }

    public TileDataParameter(DataSerializer<T> serializer, T defaultValue, ITileDataProducer<T, ? extends TileEntity> producer, ITileDataConsumer<T, ? extends TileEntity> consumer) {
        this(serializer, defaultValue, producer, consumer, null);
    }

    public TileDataParameter(DataSerializer<T> serializer, T defaultValue, ITileDataProducer<T, ? extends TileEntity> producer, ITileDataConsumer<T, ? extends TileEntity> consumer, ITileDataListener<T> listener) {
        this.value = defaultValue;
        this.serializer = serializer;
        this.valueProducer = producer;
        this.valueConsumer = consumer;
        this.listener = listener;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public DataSerializer<T> getSerializer() {
        return serializer;
    }

    public ITileDataProducer<T, ? extends TileEntity> getValueProducer() {
        return valueProducer;
    }

    public ITileDataConsumer<T, ? extends TileEntity> getValueConsumer() {
        return valueConsumer;
    }

    public void setValue(T value) {
        this.value = value;

        if (listener != null) {
            listener.onChanged(this);
        }
    }

    public T getValue() {
        return value;
    }
}