package refinedstorage.tile.data;

public interface ITileDataListener<T> {
    void onChanged(TileDataParameter<T> parameter);
}
