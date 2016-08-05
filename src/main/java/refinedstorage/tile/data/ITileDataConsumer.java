package refinedstorage.tile.data;

import net.minecraft.tileentity.TileEntity;

public interface ITileDataConsumer<T, E extends TileEntity> {
    void setValue(E tile, T value);
}
