package refinedstorage.tile.data;

import net.minecraft.network.datasync.DataSerializer;

// @TODO: Annotate me better!
public class TileDataParameter<T> {
    private int id;
    private DataSerializer<T> serializer;
    private ITileDataProducer valueProducer;
    private ITileDataConsumer valueConsumer;
    private ITileDataListener listener;
    private T value;

    public TileDataParameter(int id, DataSerializer<T> serializer, ITileDataProducer producer, ITileDataConsumer consumer, ITileDataListener listener) {
        this.id = id;
        this.serializer = serializer;
        this.valueProducer = producer;
        this.valueConsumer = consumer;
        this.listener = listener;
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

        if (listener != null) {
            listener.onChanged(this);
        }
    }

    public T getValue() {
        return value;
    }
}