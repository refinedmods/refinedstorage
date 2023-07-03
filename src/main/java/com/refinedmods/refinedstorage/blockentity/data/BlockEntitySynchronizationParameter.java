package com.refinedmods.refinedstorage.blockentity.data;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BlockEntitySynchronizationParameter<T, E extends BlockEntity> {
    private static final Set<ResourceLocation> HAD = new HashSet<>();

    private final ResourceLocation id;
    private final EntityDataSerializer<T> serializer;
    private final Function<E, T> valueProducer;
    @Nullable
    private final BiConsumer<E, T> valueConsumer;
    @Nullable
    private final BlockEntitySynchronizationClientListener<T> listener;
    private T value;

    public BlockEntitySynchronizationParameter(ResourceLocation id, EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer) {
        this(id, serializer, defaultValue, producer, null);
    }

    public BlockEntitySynchronizationParameter(ResourceLocation id, EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer, @Nullable BiConsumer<E, T> consumer) {
        this(id, serializer, defaultValue, producer, consumer, null);
    }

    public BlockEntitySynchronizationParameter(ResourceLocation id, EntityDataSerializer<T> serializer, T defaultValue, Function<E, T> producer, @Nullable BiConsumer<E, T> consumer, @Nullable BlockEntitySynchronizationClientListener<T> listener) {
        if (!HAD.add(id)) {
            throw new IllegalArgumentException("Duplicate BlockEntitySynchronizationParameter: " + id);
        }
        this.id = id;
        this.value = defaultValue;
        this.serializer = serializer;
        this.valueProducer = producer;
        this.valueConsumer = consumer;
        this.listener = listener;
    }

    public ResourceLocation getId() {
        return id;
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