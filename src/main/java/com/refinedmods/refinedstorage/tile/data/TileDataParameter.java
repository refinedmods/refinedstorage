package com.refinedmods.refinedstorage.tile.data;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TileDataParameter<T, E extends BlockEntity> {
    private final EntityDataSerializer<T> serializer;
    private final Function<E, T> valueProducer;
    @Nullable
    private final BiConsumer<E, T> valueConsumer;
    @Nullable
    private final TileDataParameterClientListener<T> listener;
    private int id;
    private T value;

    public TileDataParameter(EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer) {
        this(serializer, defaultValue, producer, null);
    }

    public TileDataParameter(EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer, @Nullable BiConsumer<E, T> consumer) {
        this(serializer, defaultValue, producer, consumer, null);
    }

    public TileDataParameter(EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer, @Nullable BiConsumer<E, T> consumer, @Nullable TileDataParameterClientListener<T> listener) {
        this.value = defaultValue;
        this.serializer = serializer;
        this.valueProducer = producer;
        this.valueConsumer = consumer;
        this.listener = listener;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EntityDataSerializer<T> getSerializer() {
        return serializer;
    }

    public Function<E, T> getValueProducer() {
        return valueProducer;
    }

    @Nullable
    public BiConsumer<E, T> getValueConsumer() {
        return valueConsumer;
    }

    public void setValue(boolean initial, T value) {
        this.value = value;

        if (listener != null) {
            listener.onChanged(initial, value);
        }
    }

    public T getValue() {
        return value;
    }
}