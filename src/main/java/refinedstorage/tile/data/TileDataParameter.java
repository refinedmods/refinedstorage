package refinedstorage.tile.data;

import net.minecraft.network.datasync.DataSerializer;

public class TileDataParameter<T> {
    private int id;
    private DataSerializer<T> serializer;
    private ITileDataProducer valueProducer;
    private ITileDataConsumer valueConsumer;
    private T value;

    public TileDataParameter(int id, DataSerializer<T> serializer, ITileDataProducer producer, ITileDataConsumer consumer) {
        this.id = id;
        this.serializer = serializer;
        this.valueProducer = producer;
        this.valueConsumer = consumer;
    }

    public int getId() {
        return id;
    }

    public DataSerializer<T> getSerializer() {
        return serializer;
    }

    public ITileDataProducer getValueProducer() {
        return valueProducer;
    }

    public ITileDataConsumer getValueConsumer() {
        return valueConsumer;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}