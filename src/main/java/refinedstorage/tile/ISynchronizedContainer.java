package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;

public interface ISynchronizedContainer {
    void readContainerData(ByteBuf buf);

    void writeContainerData(ByteBuf buf);

    Class<? extends Container> getContainer();
}
