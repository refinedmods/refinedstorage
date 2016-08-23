package refinedstorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.container.ContainerBase;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IType {
    int ITEMS = 0;
    int FLUIDS = 1;

    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, ITEMS, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IType) tile).getType();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                if (value == 0 || value == 1) {
                    ((IType) tile).setType(value);

                    // @TODO: This doesn't work serverside
                    tile.getWorld().playerEntities.stream()
                        .filter(p -> p.openContainer instanceof ContainerBase && ((ContainerBase) p.openContainer).getTile().getPos().equals(tile.getPos()))
                        .forEach(p -> p.openContainer.detectAndSendChanges());
                }
            }
        });
    }

    int getType();

    void setType(int type);

    IItemHandler getFilterInventory();
}
